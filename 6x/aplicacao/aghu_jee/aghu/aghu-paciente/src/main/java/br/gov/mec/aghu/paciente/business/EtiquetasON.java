package br.gov.mec.aghu.paciente.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesJnDAO;
import br.gov.mec.aghu.paciente.vo.ReImpressaoEtiquetasVO;

/**
 * Classe responsável por prover os métodos de negócio para geração e impressão
 * de etiquetas.
 * 
 * @author Ricardo Costa
 * 
 */
@Stateless
public class EtiquetasON extends BaseBusiness {

	private static final String COMMAND_XZ = "^XZ";
	private static final String COMMAND_FS = "^FS";
	private static final String COMMAND_PRA = "^PRA";
	private static final String COMMAND_XA = "^XA";
	private static final Log LOG = LogFactory.getLog(EtiquetasON.class);
	private static final char QUEBRA_LINHA = 10;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AipPacientesJnDAO aipPacientesJnDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7908325743665001994L;

	private enum EtiquetasONExceptionCode implements BusinessExceptionCode {

		DATA_INICIAL_MAIOR_DATA_FINAL, PACIENTE_ATENDIMENTO_NULL;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}
	}

	/**
	 * Método que gera as etiquetas.
	 * 
	 * @param prontuario
	 * @param volume
	 * @param nome
	 * @param count
	 */
	public String gerarZpl(Integer prontuario, Short volume, String nome) {
		StringBuilder zpl = new StringBuilder();
		zpl.append(this.gerarZplItem(prontuario, volume, nome, false));

		return zpl.toString();
	}

	/**
	 * Método responsável por gerar código ZPL para uma única etiqueta.
	 * 
	 * ORADB Function AIPC_GERA_ZPL
	 * 
	 * @param prontuario
	 * @param volume
	 * @param nome
	 * @param isProvisorio
	 * @return String - Texto ZPL
	 */
	private String gerarZplItem(Integer prontuario, Short volume, String nome,
			Boolean isProvisorio) {

		StringBuilder zpl = new StringBuilder(220);

		String prontAux = StringUtils.leftPad(prontuario.toString(), 8, '0');
		char primeiroDigito = prontAux.charAt(0);
		String pront = prontAux.substring(1, 7);
		char ultimoDigito = prontAux.charAt(7);
		String vol = StringUtils.leftPad(volume.toString(), 3, '0');

		zpl.append(COMMAND_XA).append(QUEBRA_LINHA).append(COMMAND_PRA).append(QUEBRA_LINHA).append("PQ01").append(QUEBRA_LINHA)
		.append("^FO060,30^ABN,18,10^FD").append(nome.trim()).append(COMMAND_FS).append(QUEBRA_LINHA)
		.append("^FO060,60^ABN,24,16^FD").append(primeiroDigito).append(COMMAND_FS).append(QUEBRA_LINHA)
		.append("^FO090,60^ABN,24,16^FD").append(pront).append(COMMAND_FS).append(QUEBRA_LINHA)
		.append("^FO210,60^ABN,24,16^FD").append(ultimoDigito).append(COMMAND_FS).append(QUEBRA_LINHA)
		.append("^FO250,60^ABN,24,16^FDVolume ").append(vol).append(COMMAND_FS).append(QUEBRA_LINHA)
		.append("^FO250,90^ABN,,07^FD").append(isProvisorio ? "PROVISORIO" : "").append(COMMAND_FS).append(QUEBRA_LINHA)
		.append("^FO060,120^BY2,,50^B2N,65,N,N^FD").append(primeiroDigito).append(pront).append(ultimoDigito)
		.append(vol).append(COMMAND_FS).append(QUEBRA_LINHA).append(COMMAND_XZ).append(QUEBRA_LINHA).append("^XA^IDR:*.TXT^XZ");

		return zpl.toString();
	}

	/**
	 * Realiza a pesquisa para re-impressão de etiquetas.
	 * 
	 * @param prontuarios
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public List<ReImpressaoEtiquetasVO> pesquisa(List<Integer> prontuarios, Date dataInicial, Date dataFinal) {
		List<Object[]> res = this.aipPacientesJnDAO.pesquisaReimpressaoEtiquetas(dataInicial, dataFinal);

		List<Object[]> listaQueryUnion = new ArrayList<Object[]>();
		List<ReImpressaoEtiquetasVO> retorno = new ArrayList<ReImpressaoEtiquetasVO>();

		if (!prontuarios.isEmpty()) {
			listaQueryUnion = this.aipPacientesDAO.pesquisaReimpressaoEtiquetasUnion(prontuarios);
		}

		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			if (!listaQueryUnion.contains(obj)) {
				listaQueryUnion.add(obj);
			}
		}

		// Criando lista de VO.
		Iterator<Object[]> it2 = listaQueryUnion.iterator();
		while (it2.hasNext()) {
			Object[] obj = it2.next();
			ReImpressaoEtiquetasVO vo = new ReImpressaoEtiquetasVO();

			if (obj[0] != null) {
				String prontAux = ((Integer) obj[0]).toString();
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)
						+ "/" + prontAux.charAt(prontAux.length() - 1));
			}
			if (obj[1] != null) {
				vo.setNome((String) obj[1]);
			}
			if (obj[2] != null) {
				vo.setLeito((String) obj[2]);
			}
			// Seta o próximo
			if (it2.hasNext()) {
				obj = it2.next();
				if (obj[0] != null) {
					String prontAux = ((Integer) obj[0]).toString();
					vo.setpProntuario(prontAux.substring(0,
							prontAux.length() - 1)
							+ "/"
							+ prontAux.charAt(prontAux.length() - 1));
				}
				if (obj[1] != null) {
					vo.setpNome((String) obj[1]);
				}
				if (obj[2] != null) {
					vo.setpLeito((String) obj[2]);
				}
			}
			retorno.add(vo);
		}
		return retorno;
	}

	/**
	 * Valida se a data inicial existe e se não é maior que a data final.
	 * 
	 * @param dtInicial
	 * @param dtFinal
	 * @throws ApplicationBusinessException
	 */
	public void validaDatas(Date dtInicial, Date dtFinal)
			throws ApplicationBusinessException {

		if (dtInicial != null && dtFinal != null && dtInicial.after(dtFinal)) {
			EtiquetasONExceptionCode.DATA_INICIAL_MAIOR_DATA_FINAL
					.throwException();
		}
	}

	/**
	 * Gera o código ZPL para impressão da pulseira do paciente
	 * 
	 * ORADB Function AINC_IMP_PULSEIRAPAC
	 * 
	 * @param paciente
	 * @param internacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String gerarEtiquetaPulseira(AipPacientes paciente, Integer atdSeq) throws ApplicationBusinessException {
		if (paciente == null) {
			throw new ApplicationBusinessException(EtiquetasONExceptionCode.PACIENTE_ATENDIMENTO_NULL);
		}
		
		Short tipoCodBarrasPulseira = (Short) parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_AGHU_TIPO_COD_BARRAS_PULSEIRA).getVlrNumerico().shortValue();
		
		if (tipoCodBarrasPulseira == 1) {
			return gerarEtiquetaPulseiraTipo1(paciente, atdSeq);
		}
		
		return gerarEtiquetaPulseiraTipo2(paciente);

	}
	
	/**
	 * Gera o código ZPL para impressão da pulseira do paciente
	 * 
	 * Código de barras tradicional (unidimensional)
	 * 
	 * @param paciente
	 * @param internacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String gerarEtiquetaPulseiraTipo1(AipPacientes paciente, Integer atdSeq) throws ApplicationBusinessException {

		if (imprimirPulseiraAdulto()) {
			return gerarCodigoZPLPulseiraAdulto(paciente, atdSeq);
		} else {
		
			StringBuilder zpl = new StringBuilder(130);
			
			// prontuario pode ser nulo
			String prontuarioStr = StringUtils.EMPTY;
			Integer prontuario = paciente.getProntuario();
			if (prontuario != null) {
				prontuarioStr = prontuario.toString();
			}
			
			String nomePaciente = paciente.getNome();
			String nomePacienteParte1 = StringUtils.EMPTY;
			String nomePacienteParte2 = StringUtils.EMPTY;
	
			if (limiteTamanhoNomePaciente(nomePaciente)) {
				nomePacienteParte1 = recuperaNomePacienteParte1(nomePaciente);
				nomePacienteParte2 = recuperaNomePacienteParte2(nomePaciente);
			} else {
				nomePacienteParte1 = nomePaciente;
			}
	
			String atdSeqStr = StringUtils.EMPTY;
			// atd_seq pode ser nulo em alguns casos
			if (atdSeq != null) {
				atdSeqStr = StringUtils.leftPad(atdSeq.toString(), 9, '0');	
			}
			String pacCodigo = recuperaCodigoPacienteString(paciente);
	
			zpl.append(COMMAND_XA).append(QUEBRA_LINHA).append(COMMAND_PRA).append(QUEBRA_LINHA)
			.append("^MD15").append(QUEBRA_LINHA).append("^MNY").append(QUEBRA_LINHA)
			.append("^BY1.4,,30").append(QUEBRA_LINHA).append("^LH0,0").append(QUEBRA_LINHA)
			.append("^LL400").append(QUEBRA_LINHA)
			.append("^FO30,020^ADN,18,10^FDPRONTUARIO: ").append(prontuarioStr)
			.append(COMMAND_FS).append(QUEBRA_LINHA)
			.append("^FO30,045^ADN,18,14^FD").append(nomePacienteParte1)
			.append(COMMAND_FS).append(QUEBRA_LINHA);
	
			if (!limiteTamanhoNomePaciente(nomePaciente)) {
				zpl.append("^FO060,075^BCN,40,N,N^FD").append(pacCodigo).append(atdSeqStr)
				.append(COMMAND_FS).append(QUEBRA_LINHA)
				.append("^FO070,125^ADN^FD").append(pacCodigo).append(atdSeqStr);
			} else {
				zpl.append("^FO30,065^ADN,18,14^FD").append(nomePacienteParte2)
				.append(COMMAND_FS).append(QUEBRA_LINHA)
				.append("^FO060,090^BCN,40,N,N^FD").append(pacCodigo).append(atdSeqStr)
				.append(COMMAND_FS).append(QUEBRA_LINHA)
				.append("^FO070,140^ADN^FD").append(pacCodigo).append(atdSeqStr);
			}
	
			zpl.append(COMMAND_FS).append(QUEBRA_LINHA).append(COMMAND_XZ).append(QUEBRA_LINHA).append("^XA^IDR:*.TXT^XZ");
	
			return zpl.toString();
		}
	}	
	
	private boolean limiteTamanhoNomePaciente(String nomePaciente) {
		return nomePaciente.length() > 50;
	}

	private Boolean imprimirPulseiraAdulto() throws ApplicationBusinessException {
		AghParametros paramImpressaoPulseira = parametroFacade.obterAghParametro(AghuParametrosEnum.P_IMPRIMIR_PULSEIRA_MODELO_ADULTO);
		return paramImpressaoPulseira != null && paramImpressaoPulseira.getVlrTexto().equalsIgnoreCase("S");
	}

	private String gerarCodigoZPLPulseiraAdulto(AipPacientes paciente, Integer atdSeq) {
		StringBuilder zpl = new StringBuilder(130);
		
		cabecalhoPulseira(zpl);
		
		conteudoPulseira(paciente, zpl);

		codigoBarrasPulseira(paciente, zpl);

		return zpl.append(COMMAND_XZ).toString();
	}

	private void conteudoPulseira(AipPacientes paciente, StringBuilder zpl) {
		if (existeProntuarioPaciente(paciente)) {
			logInfo("Prontuário do Paciente " + paciente.getProntuario());
			zpl.append("^FO240,1600^AD,20,20^FDPRONTUARIO: ").append(paciente.getProntuario()).append(COMMAND_FS).append(QUEBRA_LINHA);
		}
		zpl.append("^FO210,1600^AD,20,20^FD").append(recuperaNomePacienteParte1(paciente.getNome())).append(COMMAND_FS).append(QUEBRA_LINHA);
		if(limiteTamanhoNomePaciente(paciente.getNome())) {
			zpl.append("^FO190,1600^AD,20,20^FD").append(recuperaNomePacienteParte2(paciente.getNome())).append(COMMAND_FS).append(QUEBRA_LINHA);
		}
	}

	private boolean existeProntuarioPaciente(AipPacientes paciente) {
		return paciente.getProntuario() != null;
	}
	
	private String removeAcentos(String nome) {
		if (StringUtils.isNotBlank(nome)) {
			return StringUtil.removeCaracteresDiferentesAlfabetoEacentos(nome.toUpperCase());
		}
		return StringUtils.EMPTY;
	}

	private void cabecalhoPulseira(StringBuilder zpl) {
		zpl.append(COMMAND_XA).append(QUEBRA_LINHA)
			.append("^LH0,0").append(QUEBRA_LINHA)
			.append(COMMAND_PRA).append(QUEBRA_LINHA)
			.append("^MD15").append(QUEBRA_LINHA)
			.append("^MNY").append(QUEBRA_LINHA)
			.append("^BY2.5,,30").append(QUEBRA_LINHA)
			.append("^FWR").append(QUEBRA_LINHA);
	}

	private void codigoBarrasPulseira(AipPacientes paciente, StringBuilder zpl) {
		if (paciente.getProntuario() == null) {
			logInfo("Imprimindo código de barra pelo código do paciente");
			geraCodigoBarras(zpl, recuperaCodigoPacienteString(paciente));
		} else {
			logInfo("Imprimindo código de barra pelo número do prontuário");
			geraCodigoBarras(zpl, paciente.getProntuario().toString());
		}
	}

	private void geraCodigoBarras(StringBuilder zpl, String codigoBarra) {
		zpl.append("^FO100,1600^BC,80,N,N,N,N^FD").append(codigoBarra).append(COMMAND_FS).append(QUEBRA_LINHA)
			.append("^FO040,1600^AD,36,20^FD").append(codigoBarra).append(COMMAND_FS).append(QUEBRA_LINHA);
	}

	private String recuperaCodigoPacienteString(AipPacientes paciente) {
		return StringUtils.leftPad(paciente.getCodigo().toString(), 8, '0');
	}

	private String recuperaNomePacienteParte2(String nomePaciente) {
		if (limiteTamanhoNomePaciente(nomePaciente)) {
			return removeAcentos(nomePaciente.substring(49, nomePaciente.length()));
		}
		return StringUtils.EMPTY;
	}

	private String recuperaNomePacienteParte1(String nomePaciente) {
		if (limiteTamanhoNomePaciente(nomePaciente)) {
			return removeAcentos(nomePaciente.substring(0, 49));
		}
		return removeAcentos(nomePaciente);
	}
	
	/**
	 * Gera o código ZPL para impressão da pulseira do paciente
	 * 
	 * Código de barras - QR Code (bidimensional)
	 * 
	 * @param paciente
	 * @param internacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String gerarEtiquetaPulseiraTipo2(AipPacientes paciente) throws ApplicationBusinessException {

		StringBuilder zpl = new StringBuilder(130);
		
		// prontuario pode ser nulo
		String prontuarioStr = "";
		Integer prontuario = paciente.getProntuario();
		if (prontuario != null) {
			prontuarioStr = prontuario.toString();
		}
		
		String nome = paciente.getNome();
		String nomeParte1 = "";
		String nomeParte2 = "";

		if (nome.length() > 25) {
			nomeParte1 = nome.substring(0, 24);
			nomeParte2 = nome.substring(24, nome.length());
		} else {
			nomeParte1 = nome;
		}

		String pacCodigo = recuperaCodigoPacienteString(paciente);

		zpl.append(COMMAND_XA).append(QUEBRA_LINHA)
		.append(COMMAND_PRA).append(QUEBRA_LINHA)
		.append("^MD15").append(QUEBRA_LINHA)
		.append("^MNY").append(QUEBRA_LINHA)
		.append("^BY1.4,,30").append(QUEBRA_LINHA)
		.append("^LH0,0").append(QUEBRA_LINHA)
		.append("^LL400").append(QUEBRA_LINHA)
		.append("^FO020,045^ARN,11,07^FD").append(nomeParte1).append(COMMAND_FS).append(QUEBRA_LINHA)
		.append("^FO020,075^ARN,11,07^FD").append(nomeParte2).append(COMMAND_FS).append(QUEBRA_LINHA);
        
        if (prontuario == null) {
        	zpl.append("^FO020,025^ADN,11,07^FDCODIGO:^FS").append(QUEBRA_LINHA)
        	.append("^FO180,015^ARN,,07^FD").append(pacCodigo).append(COMMAND_FS).append(QUEBRA_LINHA)
        	.append("^FO020,105^BY2,,10^BQN,2,3,N,N^FD000").append(pacCodigo).append(COMMAND_FS).append(QUEBRA_LINHA);
        } else {
        	zpl.append("^FO020,025^ADN,11,07^FDPRONTUARIO:^FS").append(QUEBRA_LINHA)
        	.append("^FO170,015^ARN,,07^FD").append(prontuarioStr).append(COMMAND_FS).append(QUEBRA_LINHA)
        	.append("^FO020,105^BY2,,10^BQN,2,3,N,N^FD000").append(prontuarioStr).append(COMMAND_FS).append(QUEBRA_LINHA);
        }
                
        zpl.append(COMMAND_XZ).append(QUEBRA_LINHA).append("^XA^IDR:*.TXT^XZ"); 
                
        return zpl.toString();
	}		
	
}
