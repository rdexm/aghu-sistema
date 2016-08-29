package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioPortalPlanejamentoCirurgiasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioPortalPlanejamentoCirurgiasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	private static final Integer QTD_MIN_PALAVRAS = 1;

	private static final long serialVersionUID = 7380212095972642680L;

	/**
	 * @ORADB FUNCTION AELC_TRATA_NOME
	 * 
	 *        Retorna nomes de pessoas com ponto no nomes intermediários. 
	 *        
	 *        Em alguns casos a função do ORACLE retorna o símbolo “_” no nome do
	 *        paciente. Este é um problema na função do ORACLE e para a
	 *        implementação do sistema este símbolo não deve ser inserido no
	 *        nome do paciente conforme validado com o consultor do módulo.
	 * 
	 */
	public String obterNomeIntermediarioPacienteAbreviado(String nomePaciente){//public static -> para teste
		//Remove múltiplos espaços entre os nomes
		//nomePaciente = nomePaciente.replaceAll("\\b\\s{2,}\\b", " ");
		
		
		if(nomePaciente == null || nomePaciente.replaceAll(" ", "").length() == 0 ){
			return " ";
		}
		//Remove 'conectores'
		List<String> conectores = Arrays.asList(" DA ", " Da ", " dA ", " da ",
												" DE ", " De ", " dE ", " de ",
												" DI ", " Di ", " dI ", " di ",
												" DO ", " Do ", " do ", " do ",
												" DOS ", " DOs ", " Dos ", " DoS ",
												" dOS ", " dOs ", " doS ", " dos ",
												" DAS ", " DAs ", " Das ", " DaS ",
												" dAS ", " dAs ", " daS ", " das "
												);
		
		for(String conec: conectores){
			nomePaciente = nomePaciente.replaceAll(conec, " ");
		}
		
		StringTokenizer token = new StringTokenizer(nomePaciente, " ");
		
		Integer quantidadePalavras = token.countTokens(); //quantidade de palavras ou tokens
		StringBuilder nomePacienteFormatado = new StringBuilder(200);
		
		
		int nToken=1; // incremento para a quantidade de tokens utilizadas
		String primeiroToken = token.nextToken();
		
		nToken++;
		if(primeiroToken.toUpperCase().startsWith("RN")){
			nomePacienteFormatado.append(primeiroToken.toUpperCase()).append(' '); //RN, RNI, RNII, RNIII
			String segundoToken= token.nextToken();		
			nToken++;	
			
			if (validaNumerosRomanos(segundoToken)){
				nomePacienteFormatado.append(segundoToken.toUpperCase()).append(' '); //RN, RNI, RNII, RNIII
				nomePacienteFormatado.append(token.nextToken()).append(' ');
				nToken++;
			}else{
				nomePacienteFormatado.append(segundoToken).append(' ');
			}
		}else{
			nomePacienteFormatado.append(primeiroToken).append(' '); //1o nome
		}
		
		while(token.hasMoreElements() && nToken < quantidadePalavras){
			nToken++;
			String abreviacao = token.nextToken();
			if(abreviacao.length()>1){
				nomePacienteFormatado.append(abreviacao.substring(0, 1)).append('.');
			}
		}
		
		if (quantidadePalavras > QTD_MIN_PALAVRAS) {
			nomePacienteFormatado.append(token.nextToken()); //armazena último nome
		}
		return nomePacienteFormatado.toString();
	}
	
	public Boolean validaNumerosRomanos(String token){ //public static -> para teste
		String[] NUMEROS_ROMANOS_ACEITOS = new String[]{"I", "II", "III"};
		if(Arrays.asList(NUMEROS_ROMANOS_ACEITOS).contains(token.toUpperCase())){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	
	/**
	 * @ORADB FUNCTION CF_1Formula
	 * 
	 * Retorna o tempo de sala em hh24:mi
	 * 
	 */
	public String obterTempoSalaCirurgia(Integer agdSeq, Byte intervaloEscala){
	
	Integer diferencaEmHoras = 0;
	Integer minutosRestantes = 0;

	Integer vTempoMinCirgRealizada;
	String vTempoSala  = null;
	
	vTempoMinCirgRealizada = obterTempoMinimoCirurgia(agdSeq, intervaloEscala); 
	         
	if(vTempoMinCirgRealizada > 0){
		
		diferencaEmHoras = vTempoMinCirgRealizada / 60;
		minutosRestantes = vTempoMinCirgRealizada % 60;
		
		vTempoSala =  String.format("%02d",diferencaEmHoras) + ":" + String.format("%02d",minutosRestantes);
	}

	return vTempoSala;
	}

	/**
	 * @ORADB FUNCTION MBCC_VER_REALIZADA 
	 */
	 public Integer obterTempoMinimoCirurgia(Integer agdSeq, Byte intervaloEscala) {
		 Integer vTempoMinCirRealizada = 0;
		 
		 MbcCirurgias cirurgia = this.getBlocoCirurgicoFacade().verificarSeAgendamentoTemCirurgiaRealizada(agdSeq);
			
		 if(cirurgia != null){
			 
			 //MbcDescricaoItens
			 LinhaReportVO data = this.getBlocoCirurgicoFacade().obterDataInicioFimCirurgia(cirurgia.getSeq(), DateUtil.obterData(2004, 1, 1));
			 
			 if(data != null && data.getData() != null && data.getData1() != null){
				 BigDecimal vTempoMinDescCirg = DateUtil.calcularDiasEntreDatasComPrecisao(data.getData(), data.getData1());
				 vTempoMinDescCirg = vTempoMinDescCirg.multiply(new BigDecimal(24).multiply(new BigDecimal(60)));
				 vTempoMinCirRealizada = vTempoMinDescCirg.intValue() + intervaloEscala;
			 }else{
				 BigDecimal vTempoMinCirg;
				 if(cirurgia.getDataSaidaSala() == null || cirurgia.getDataEntradaSala() == null){
					 //vTempoMinCirg
					 vTempoMinCirg = DateUtil.calcularDiasEntreDatasComPrecisao(cirurgia.getDataFimCirurgia(),cirurgia.getDataInicioCirurgia());
					 vTempoMinCirg = vTempoMinCirg.multiply(new BigDecimal(24).multiply(new BigDecimal(60)));
					 vTempoMinCirRealizada = vTempoMinCirg.intValue();
					 if(vTempoMinCirRealizada != 0){
						 return vTempoMinCirRealizada;
					 }
				 }
				
				 vTempoMinCirg = DateUtil.calcularDiasEntreDatasComPrecisao(cirurgia.getDataSaidaSala(),cirurgia.getDataEntradaSala());
				 vTempoMinCirg = vTempoMinCirg.multiply(new BigDecimal(24).multiply(new BigDecimal(60)));
				 vTempoMinCirRealizada = vTempoMinCirg.intValue();
			 }
		 }
		 return vTempoMinCirRealizada;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		 return this.iBlocoCirurgicoFacade;
	}
}
