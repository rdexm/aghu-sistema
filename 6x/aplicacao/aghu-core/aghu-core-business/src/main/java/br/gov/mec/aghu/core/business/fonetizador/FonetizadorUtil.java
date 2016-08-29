package br.gov.mec.aghu.core.business.fonetizador;

import java.text.Normalizer;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class FonetizadorUtil {

    private static final int MAXTAMTEXTO = 18; /* Tamanho máximo do texto */
    private static final int MAXTAMFONEMA = 6; /* Tamanho máximo do fonema */

    private static char[] tabTr = {10, 1, 2, 3, 4, 5, 2, 0, 4, 8, 2, 9, 11, 11, 12, 1, 2, 13, 14, 3, 12, 5, 5, 15, 4, 14, 0};
    private static char[] tabA = {1, 2, 3, 5, 8, 0xFF};
    private static char[] tabB = {10, 4, 12, 9, 13, 0xFF};
    private static char[] tabC = {10, 4, 12, 0xFF};
    private static char[] fonet = new char[MAXTAMTEXTO * 2];
    
	private enum FonetizadorUtilExceptionCode implements BusinessExceptionCode {
		ERRO_TAMANHO_MAXIMO_FONEMA 
	}    

    /**
     * Retorna o fonema referente ao nome especificado.
     * @param nome nome a ser fonetizado.
     * @return fonema referente ao nome informado.
     */
    public static String obterFonema(String nome) throws ApplicationBusinessException {
    	if (StringUtils.isBlank(nome)) {
    		return null;
    	}
        
    	return parse(ajustarNome(nome));
    }

    /**
     * Realiza o parse do nome e retorna o fonema.
     * @param str nome a ser fonetizado.
     * @return fonema referente ao nome informado.
     */
    private static String parse(String str) throws ApplicationBusinessException {
        StringBuilder ret = new StringBuilder("");

        StringTokenizer st = new StringTokenizer(str, " ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (!token.equals("DE") && !token.equals("DAS") && !token.equals("DOS") && !token.equals("DA") &&
                !token.equals("DO") && !token.equals("RN") && !token.equals("RN1") && !token.equals("RN2")) {

            	if (token.length() >= MAXTAMTEXTO * 2) { 
            		throw new ApplicationBusinessException(FonetizadorUtilExceptionCode.ERRO_TAMANHO_MAXIMO_FONEMA, ((MAXTAMTEXTO * 2)-1), token); 
            	}
            	
                fonetizar(token.toCharArray());
                for (int i = 0; i < MAXTAMFONEMA; i++) {
                    ret.append(Integer.toHexString(fonet[i]).toUpperCase());
                }
            }
        }

        return (ret.toString().trim().length() > 60) ? ret.toString().trim().substring(0, 60) : ret.toString().trim();
    }

    /**
     * Realiza a fonetização de cada partícula do nome informado.
     * @param txtArray partícula a ser fonetizada.
     */
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
    private static void fonetizar(char[] txtArray) {
    	char[] tmpArray = new char[60];
        System.arraycopy(txtArray, 0, tmpArray, 0, txtArray.length);

        tmpArray[txtArray.length] = '\0';
        tmpArray[txtArray.length + 1] = '\0';

        /* Substitui palavras terminadas em 'AO' por 'AM' */
        if (txtArray.length > 1 && tmpArray[txtArray.length - 2] == 'A' && tmpArray[txtArray.length - 1] == 'O') {
            tmpArray[txtArray.length - 1] = 'M';
        }

        int i = 0;

        for (i = 0; i < txtArray.length; i++) {
            switch (tmpArray[i]) {
                case 'C':
                    switch (tmpArray[i + 1]) {
                        case 'E':
                        case 'I':
                        case 'Y':
                            tmpArray[i++] = 'S'; /* C + E, I, Y -> S + E, I, Y */
                            break;

                        case 'T':
                            tmpArray[i++] = 'T'; /* CT -> TT */
                            break;

                        case 'H':
                            if (tmpArray[i + 2] == 'R') /* CHR -> CRR */ {
                                tmpArray[++i] = 'R';
                            } else {
                                tmpArray[i] = 'X';
                                tmpArray[++i] = 'X';
                            }
                            break;
                    }
                    break;

                case 'G':
                    switch (tmpArray[i + 1]) {
                        case 'E':
                        case 'I':
                        case 'Y':
                            tmpArray[i] = 'J'; /* G + E, I, Y -> J + E, I, Y */

                            if (tmpArray[i + 2] == 'A' || tmpArray[i + 2] == 'O' || tmpArray[i + 2] == 'U') {
                                tmpArray[++i] = 'J';  /* G + E, I, Y + A, O, U -> JJ + A, O, U */
                            }
                            break;

                        case 'U':
                            if (tmpArray[i + 2] == 'E' || tmpArray[i + 2] == 'I' || tmpArray[i + 2] == 'Y') {
                                tmpArray[++i] = 'G';  /* GU + E, I, Y -> GG + E, I, Y */
                            }
                            break;
                    }
                    break;

                case 'H':
                    if ((txtArray.length - 1) == 0) {
                        tmpArray[i] = 'Z' + 1; /* Só H: letra acima do Z */
                    } else {
                        tmpArray[i] = tmpArray[(i == 0 ? 1 : i - 1)];   /* H + ?, ? + H -> ? + ? */
                    }
                    break;

                case 'L':
                    if (i == 0) {
                        break;
                    }

                    switch (tmpArray[i + 1]) {
                        case 'A':
                        case 'E':
                        case 'I':
                        case 'O':
                        case 'U':
                        case 'Y':
                            break; /* L normal com A, E, I, O, U, L, Y */

                        case 'H':
                            tmpArray[++i] = 'I'; /* LH -> LI */
                            break;

                        case 'L':
                            switch (tmpArray[i + 2]) {
                                case 'A':
                                case 'E':
                                case 'I':
                                case 'O':
                                case 'U':
                                case 'H':
                                case 'L':
                                case 'Y':
                                    break; /* LL normal com A, E, I, O, U, L, Y */

                                default:
                                    tmpArray[i] = 'R'; /* outros casos LL + ? -> RL + ? */
                            }
                            break;

                        default:
                            tmpArray[i] = 'R'; /* outros casos L + ? -> R + ? */
                    }
                    break;

                case 'P':
                    switch (tmpArray[i + 1]) {
                        case 'C':
                        case 'T':
                        case 'F':
                            tmpArray[i++] = tmpArray[i]; /* P + C, T, F -> CC, TT, FF */
                            break;

                        case 'H':
                            tmpArray[i] = 'F'; /* P + H -> FF */
                            tmpArray[++i] = 'F';
                            break;
                    }
                    break;

                case 'Q':
                    if (tmpArray[i + 1] == 'U') {
                        switch (tmpArray[i + 2]) {
                            case 'E':
                            	 tmpArray[++i] = 'Q'; /* QU + E, I, Y -> QQ + E, I, Y */
                            	 break;
                            case 'I':
                            	 tmpArray[++i] = 'Q'; /* QU + E, I, Y -> QQ + E, I, Y */
                            	 break;
                            case 'Y':
                                tmpArray[++i] = 'Q'; /* QU + E, I, Y -> QQ + E, I, Y */
                                break;
                        }
                    }
                    break;

                case 'S':
                    switch (tmpArray[i + 1]) {
                        case 'H':
                            tmpArray[i] = 'X'; /* SH -> XX */
                            tmpArray[++i] = 'X';
                            break;

                        case 'C':
                            if (tmpArray[i + 2] == 'H') {
                                tmpArray[i] = 'X'; /* SCH -> XXX */
                                tmpArray[++i] = 'X';
                                tmpArray[++i] = 'X';
                            }
                            break;
                    }

                    if (i == 0) {
                        switch (tmpArray[i + 1]) {
                            case 'A':
                            case 'E':
                            case 'I':
                            case 'O':
                            case 'U':
                            case 'Y':
                            case 'H':
                            case 'S':
                            case 'X':
                            case 'Z':
                                break;

                            default:
                                tmpArray[i++] = '*'; /* S + ? -> * + ? (inserir um I) */
                        }
                    } else {
                        if (tmpArray[i + 1] == 'C') {
                            switch (tmpArray[i + 2]) {
                                case 'E':
                                	 tmpArray[++i] = 'S'; /* SC + E, I, Y -> SS + E, I, Y */
                                	 break;
                                case 'I':
                                	 tmpArray[++i] = 'S'; /* SC + E, I, Y -> SS + E, I, Y */
                                	 break;
                                case 'Y':
                                    tmpArray[++i] = 'S'; /* SC + E, I, Y -> SS + E, I, Y */
                                    break;
                            }
                        }
                    }
                    break;
            }
        }

        /* Insere os casos especiais do I (caso *) e traduz para hexadecimal */
        char[] trab = new char[MAXTAMTEXTO * 2];
        int j;

        for (j = 0, i = 0; i < txtArray.length; i++) {
            if (tmpArray[i] == '*') {
                trab[j++] = tabTr['S' - 'A'];
                trab[j++] = tabTr['I' - 'A'];
            } else {
        		trab[j++] = tabTr[tmpArray[i] - 'A'];
            }
        }

        trab[j--] = 0xFE; /* Garante testes "limpos" */

        int tamFon, l, k;
        for (i = tamFon = 0; i <= j; i++) {
            if (i < j && trab[i] == trab[i + 1]) {
                continue;
            }
            
            // FIXME tive que colocar esse teste para evitar um ArrayIndexOutOfBoundsException, verificar se é erro no algoritmo ou na migração
            if (tamFon >= MAXTAMTEXTO * 2) {
            	break;
            }

            fonet[tamFon++] = trab[i];
            for (k = 0; tabA[k] != 0xFF; k++) {
                if (trab[i] == tabA[k]) {
                    for (l = 0; tabB[l] != 0xFF && trab[i + 1] != tabB[l]; l++) {
						;
					}
                    if (tabB[l] == 0xFF) {
                        fonet[tamFon++] = 4; /* Insere um 'I' */
                    }
                    break;
                }
            }

            if (tabA[k] == 0xFF && trab[i] == 15) {
                for (l = 0; tabC[l] != 0xFF && trab[i + 1] != tabC[l]; l++) {
					;
				}
                if (tabC[l] == 0xFF) {
                    fonet[tamFon++] = 4; /* Insere um 'I' na colisão com 'X' */
                }
            }
        }

        for (i = tamFon; i <= MAXTAMTEXTO; i++) {
            fonet[i] = '\0'; /* Limpa o resto do fonema */
        }
    }

    /**
     * Método responsável por retirar espaços em branco duplo, caracteres especiais, números, acentos e fazer um uppercase da string.
     * 
     * @param str valor a ser ajustado.
     * @return valor ajustado.
     */
    public static String ajustarNome(String str) {
    	//[Ç], C - Substitui Ç por C.
        str = str.toUpperCase().replaceAll("[Ç]", "C");
        
        /* Retira acentos */
        str = Normalizer.normalize(str.subSequence(0, str.length()),
                Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        /* Retira caracteres especiais(+, -, [, ], {, }, etc) */
        str = str.replaceAll("[^ ABCDEFGHIJKLMNOPQRSTUVWXYZ]", "").trim();
        
        /* Insere espaço em branco caso a string seja duas vezes maior ou igual que o tamanho permitido*/
        if ((str.length() >= MAXTAMTEXTO * 2) && !str.contains(" ")){
        	StringBuilder regex = new StringBuilder();
        	for (int cont = 0; cont < MAXTAMTEXTO; cont ++) {
        		regex.append(".");
        	}
        	str = str.replaceAll(regex.toString(), "$0 ");
        }
        
        /* Retira os espaços duplos */
        return str.replaceAll("\\s{2,}", " ");
    }
}