package GameEngine;

import java.util.*;

/**
 * 20장 덱과 참가자의 패를 관리하고 족보 계산, 봇 패 선택 및 승자 판정을 수행한다.
 */
public class Engine{
	
	GameState state;
	List<Integer> deck;
	List<List<Integer>> cards; 
	int[] result;
	private boolean bottomDealUsed;
	
    final int SAM_PAL = 33; 
    final int IL_PAL = 32;
    final int IL_SAM= 31; 
    final int ALI = 16;
    final int DOKSA = 15;
    final int GU_PPING = 14;
    final int JANG_PPING = 13;
    final int JANG_SA = 12;
    final int SAERYUK = 11;
    final int AMHANGEOSA = 41;
    final int MEONGGUSA = 42;
    final int TTAENGJAPYI = 43;
    final int GUSA = 44;
	
	/**
	 * 현재 인원과 모드에 맞는 패 및 족보 배열을 초기화한다.
	 *
	 * @param state 현재 게임의 공유 상태
	 */
	public Engine(GameState state){
		this.state = state;
		cards = new ArrayList<>();
		for(int i = 0; i < state.getPeople(); i++) {
			List<Integer> temp = new ArrayList<>();
			for(int j = 0; j < state.getPaper(); j++) {
				temp.add(0);
			}
			cards.add(temp);
		}
		result = new int[state.getPeople()];
	}
	
	/**
	 * 1~20번으로 이루어진 중복 없는 덱을 생성하고 섞는다.
	 */
	public void makeDeck() {
		deck = new ArrayList<>();
		for(int i = 1; i < 21; i++) {
			deck.add(i);
		}
		Collections.shuffle(deck);
	}
	
	/**
	 * 기본 패를 분배하고 밑장빼기를 사용한 담당자에게 한 장을 더 준다. 담당 봇의 사용 확률은 30%이다.
	 *
	 * @param useBottomDeal 사용자 담당 판의 밑장빼기 선택 여부
	 */
	public void distribute(boolean useBottomDeal) {
		this.bottomDealUsed = state.getLastWinner() == 0 ? useBottomDeal : new Random().nextInt(100) < 30;
		int cnt = 0;
		for(int i = 0; i < state.getPeople(); i++) {
			cards.get(i).clear();
			for(int j = 0; j < state.getPaper(); j++) {
				cards.get(i).add(deck.get(cnt++));
			}
		}
		if(this.bottomDealUsed) {
			cards.get(state.getLastWinner()).add(deck.get(cnt));
		}
	}
	/**
	 * 가장 최근 분배에서 담당자가 밑장빼기를 사용했는지 반환한다.
	 * @return 조건을 만족하면 true
	 */
	public boolean isBottomDealUsed() {
		return bottomDealUsed;
	}
	
	/**
	 * 지정한 참가자가 현재 가진 패의 장수를 반환한다.
	 *
	 * @param row 참가자 번호. 0은 사용자, 1부터는 봇
	 * @return 현재 상태의 값
	 */
	public int getCardCount(int row) {
		return cards.get(row).size();
	}
	
	/**
	 * 지정한 참가자의 해당 위치에 있는 카드 번호를 문자열로 반환한다.
	 *
	 * @param row 참가자 번호. 0은 사용자, 1부터는 봇
	 * @param col 0부터 시작하는 패의 위치
	 * @return 카드 번호의 십진수 문자열
	 */
	public String getIndex(int row, int col) {
		
		int value = cards.get(row).get(col);
		
		return Integer.toString(value);
	}
    /**
     * 두 패의 족보 값을 계산한다. one이 0이면 참가자의 앞 두 패를 사용한다. 유효한 참가자 번호일 때만 결과를 저장한다.
     *
     * @param one 첫 카드 번호. 0이면 참가자의 앞 두 패 사용
     * @param two 두 번째 카드 번호
     * @param i 결과를 저장할 참가자 번호. 500이면 계산만 수행
     * @return 일반 또는 특수 족보를 나타내는 내부 정수 값
     */
    public int convertIndex(int one, int two, int i) {
        if(one == 0){
            one = cards.get(i).get(0);
            two = cards.get(i).get(1);
        }
        int answer = evaluateHand(one, two);
        if(0 <= i && i < result.length) result[i] = answer;
        return answer;
    }

    /**
     * 두 카드의 족보를 계산한다. 손패 순서와 참가자의 계산 결과는 변경하지 않는다.
     * @param one 첫 카드 번호 1~20
     * @param two 두 번째 카드 번호 1~20
     * @return 일반 또는 특수 족보를 나타내는 내부 정수 값
     */
    public int evaluateHand(int one, int two) {
        int first;
        int second;
        int answer;
        if(one > two){
            first = two;
            second = one;
        }
        else{
            first = one;
            second = two;
        }
        if(first == 13 && second == 18){
            answer = SAM_PAL;
        }
        else if(first == 11 && second == 18){
            answer = IL_PAL;
        }
        else if(first == 11 && second == 13){
            answer = IL_SAM;
        }
        else if(first == 14 && second == 17){
            answer = AMHANGEOSA;
        }
        else if(first == 13 && second == 17){
            answer = TTAENGJAPYI;
        }   
        else if(first == 14 && second == 19){
            answer = MEONGGUSA;
        }
        else{
            if(second - first == 10){
                int ttaeng = 20 + first;
                answer = ttaeng;
            }
            else{
                int[] temp = new int[2];
                temp[0] = first;
                temp[1] = second;
                if(first > 10){
                    temp[0] -= 10;
                    temp[1] -= 10;
                }
                else if(second > 10){
                    temp[1] -= 10;
                }
                Arrays.sort(temp);
                if(temp[0] == 1 && temp[1] == 2){
                    answer = ALI;
                }
                else if(temp[0] == 1 && temp[1] == 4){
                    answer = DOKSA;
                }
                else if(temp[0] == 1 && temp[1] == 9){
                    answer = GU_PPING;
                }
                else if(temp[0] == 1 && temp[1] == 10){
                    answer = JANG_PPING;
                }
                else if(temp[0] == 4 && temp[1] == 10){
                    answer = JANG_SA;
                }
                else if(temp[0] == 4 && temp[1] == 6){
                    answer = SAERYUK;
                }
                else if(temp[0] == 4 && temp[1] == 9){
                    answer = GUSA;
                }

                else{
                    int ggeut = temp[0] + temp[1];
                    if(ggeut >= 10){
                        ggeut -= 10;
                    }
                    answer = ggeut;
                }
            }
       
        }
        return answer;
    } 
    
    /**
     * 일반 및 특수 족보와 동률을 비교하여 GameState의 승자 또는 재경기 결과를 설정한다.
     */
    public void whoIsWinner(){
        int winIndex = -1;
        boolean overlap = false;
        boolean replayRequired = false;
        int general = -1;
        HashMap<Integer, Integer> specials = new HashMap<>();

        for(int i = 0; i < state.getPeople(); i++){ 
            if(general < result[i] && result[i] < 40){
                general = result[i];
                winIndex = i;
            }
            else if(result[i] > 40){
                specials.put(i, result[i]);
            }
        }
        for(int i = 0; i < state.getPeople(); i++){
            if(winIndex == i){
                ;
            }
            else if(general == result[i]){
                overlap = true;
            }
        }

        if(specials.size() >= 1){
        	Set<Map.Entry<Integer, Integer>> entries = specials.entrySet();
        	List<Map.Entry<Integer, Integer>> sortedSpecials = new ArrayList<>(entries);
        	sortedSpecials.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));
            for(Map.Entry<Integer, Integer> entry : sortedSpecials){
                int i = entry.getKey();
                    if(specials.get(i) == AMHANGEOSA && IL_SAM <= general && general <= IL_PAL){
                        winIndex = i;
                    }
                    else if(specials.get(i) == TTAENGJAPYI && 21 <= general && general <= 29){
                        winIndex = i;
                    }
                    else if(specials.get(i) == GUSA && general <= ALI){
                        replayRequired = true;
                    }
                    else if(specials.get(i) == MEONGGUSA && general <=29){
                        replayRequired = true;
                    }
                    else if(specials.get(i) == AMHANGEOSA && (general < IL_SAM || IL_PAL < general)){
                        if(general == 1){
                            winIndex = 6;
                        }
                        else if(general == 0){
                            general = 1;
                            winIndex = i;
                        }
                        else if(overlap == true){
                            winIndex = 6;
                        }
                    }
                    else if(specials.get(i) == TTAENGJAPYI && (general < 21 || 29 < general)){
                        if(general == 0){
                            winIndex = 6;
                        }
                        else if(overlap == true){
                            winIndex = 6;
                        }
                    }
            }
        }
        else{
            if (overlap == true){
                winIndex = 6;
            }
        }
        state.setWinner(replayRequired ? 6 : winIndex);
    }
    
	/**
	 * 지정한 참가자의 버릴 패를 제거한다.
	 *
	 * @param row 참가자 번호. 0은 사용자, 1부터는 봇
	 * @param index 0부터 시작하는 버릴 패의 위치
	 */
	public void selectingCard(int row, int index) {
		if(cards.get(row).size() <= 2) return;
		cards.get(row).remove(index);
		if(cards.get(row).size() == 2) {
			convertIndex(0, 0, row);
		}
	}
	/**
	 * 일반 족보 값이 가장 높은 조합을 우선하여 봇의 패를 두 장으로 줄이고 족보를 저장한다.
	 *
	 * @param row 참가자 번호. 0은 사용자, 1부터는 봇
	 */
	public void selectBotCards(int row) {
		int max = -1;
		int mI = 0;
		int mJ = 1;
		for(int i = 0; i < cards.get(row).size() - 1; i++) {
			for(int j = i + 1; j < cards.get(row).size(); j++) {
				int r = evaluateHand(cards.get(row).get(i), cards.get(row).get(j));
				if(r > max && r < 40) {
					max = r;
					mI = i;
					mJ = j;
				}
			}
		}
		for(int i = cards.get(row).size() - 1; i >= 0; i--) {
			if(i != mI && i != mJ) {
				cards.get(row).remove(i);
			}
		}
		convertIndex(0, 0, row);
	}
	
	
	/**
	 * 두 카드의 표시용 족보 이름을 반환하며 참가자의 계산 결과를 변경하지 않는다.
	 *
	 * @param first 첫 카드 번호 1~20
	 * @param second 두 번째 카드 번호 1~20
	 * @return 표시용 족보 이름
	 */
	public String handName(int first, int second) {
		return resultToString(evaluateHand(first, second));
	}

	private String resultToString(int value) {
		switch(value) {
		case 0:
			return "망통";
		case SAERYUK:
			return "세륙";
		case JANG_SA:
			return "장사";
		case JANG_PPING:
			return "장삥";
		case GU_PPING:
			return "구삥";
		case DOKSA:
			return "독사";
		case ALI:
			return "알리";
		case IL_SAM:
			return "13광땡";
		case IL_PAL:
			return "18광땡";
		case SAM_PAL:
			return "38광땡";
		case AMHANGEOSA:
			return "암행어사";
		case TTAENGJAPYI:
			return "땡잡이";
		case GUSA:
			return "구사";
		case MEONGGUSA:
			return "멍구사";
		default:
			if(21 <= value && value <= 30) {
				return (value - 20) + "땡";
			}
			return value + "끗";
		}
	}
	
}

