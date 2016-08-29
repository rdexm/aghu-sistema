package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOcorrenciaFichaEvento;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoOcorrenciaFichaFarmaco;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.OcorrenciaFichaFarmacoGasesVO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghDocumentosAssinados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaFarmaco;
import br.gov.mec.aghu.model.MbcFichaGas;
import br.gov.mec.aghu.model.MbcFichaMedMonitorizacao;
import br.gov.mec.aghu.model.MbcFichaPaciente;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaEvento;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaFarmaco;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaGas;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcTmpFichaFarmaco;
import br.gov.mec.aghu.model.MbcTmpFichaFarmacoId;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.FichaFarmacoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class CirurgiasInternacaoPOLRN extends BaseBusiness {

	private static final String HIFEN_HIFEN_PAR = "--)";

	@EJB
	private ConsultaNotasPolRN consultaNotasPolRN;
	
	private static final Log LOG = LogFactory.getLog(CirurgiasInternacaoPOLRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private static final long serialVersionUID = -8680975932835942803L;
	public enum CirurgiasInternacaoPOLRNExceptionCode implements BusinessExceptionCode {
		ERRO_GERAR_RELATORIO_ATOS_ANESTESICOS_FICHA_NULA, ERRO_GERAR_RELATORIO_ATOS_ANESTESICOS_VGLOBAL
	}

	/**
	 * RN0 - habilitar botões: botaoDescricao, botaoAtoAnestesico,
	 * botaoExameAnatomopatologico, botaoDocAssinado
	 * 
	 * @author heliz
	 */

	public Boolean habilitarBotaoDescricao(Integer seqCirurgia) {

		Boolean prntolAdmin = recuperarGlobalAipfPrntolAdmin();

		Boolean botaoDescricao = null;

		if (prntolAdmin == null) {
//			atribuirContextoSessao(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN,
//					Boolean.FALSE);// prntol_admin = "N"
			prntolAdmin = Boolean.FALSE;
		}

		DominioSituacaoDescricaoCirurgia situacaoDescricaoCirurgia = DominioSituacaoDescricaoCirurgia.CON;
		//String situacaoDescricaoCirurgia = "CON";
		DominioSituacaoDescricao[] situacaoDescricao = new DominioSituacaoDescricao[] {
				DominioSituacaoDescricao.PRE, DominioSituacaoDescricao.DEF };

		List<MbcDescricaoCirurgica> descricaoCirurgica = getBlocoCirurgicoFacade()
				.listarDescricaoCirurgicaPorSeqCirurgiaSituacao(seqCirurgia,
						situacaoDescricaoCirurgia);
		List<PdtDescricao> descricao = getBlocoCirurgicoProcDiagTerapFacade()
				.listarDescricaoPorSeqCirurgiaSituacao(seqCirurgia,
						situacaoDescricao);

		if (prntolAdmin == Boolean.FALSE) {
			if (descricaoCirurgica == null || descricaoCirurgica.isEmpty()) {
				if (descricao == null || descricao.isEmpty()) {
					botaoDescricao = Boolean.FALSE;
				} else {
					if (prntolAdmin == Boolean.FALSE) {
						botaoDescricao = Boolean.TRUE;
					}
				}
			} else {
				if (prntolAdmin == Boolean.FALSE) {
					botaoDescricao = Boolean.TRUE;
				} else {
					botaoDescricao = Boolean.FALSE;
				}
			}
		}

		return botaoDescricao;
	}

	public Boolean recuperarGlobalAipfPrntolAdmin() {
		return Boolean.FALSE;
		//return (Boolean) obterContextoSessao("AIPF_PRNTOL_ADMIN"); // variável
		// global
	}

	public Boolean habilitarBotaoAtoAnestesico(Integer seqCirurgia) {

		Boolean prntolAdmin = recuperarGlobalAipfPrntolAdmin();

		Boolean botaoAtoAnestesico = null;

		if (prntolAdmin == null) {
//			atribuirContextoSessao(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN,
//					Boolean.FALSE);// prntol_admin = "N"
			prntolAdmin = Boolean.FALSE;
		}

		//String pendente = "V";
		DominioIndPendenteAmbulatorio pendente = DominioIndPendenteAmbulatorio.V;

		List<MbcFichaAnestesias> fichaAnestesia = blocoCirurgicoFacade.listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(seqCirurgia, pendente);

		if (prntolAdmin == Boolean.FALSE) {
			if (fichaAnestesia == null || fichaAnestesia.isEmpty()) {
				botaoAtoAnestesico = Boolean.FALSE;
			} else {
				if (prntolAdmin == Boolean.FALSE) {
					botaoAtoAnestesico = Boolean.TRUE;
				} else {
					botaoAtoAnestesico = Boolean.FALSE;
				}
			}
		}

		return botaoAtoAnestesico;
	}

	public Boolean habilitarBotaoExameAnatopatologico(Integer seqCirurgia)
			throws ApplicationBusinessException {

		Boolean prntolAdmin = recuperarGlobalAipfPrntolAdmin();

		Boolean botaoExameAnatomopatologico = null;

		if (prntolAdmin == null) {
//			atribuirContextoSessao(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN,
//					Boolean.FALSE);// prntol_admin = "N"
			prntolAdmin = Boolean.FALSE;
		}

		AghParametros paramSituacaoLiberado = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros paramSituacaoExecutando = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);

		String v_situacao_liberado = paramSituacaoLiberado.getVlrTexto()
				.substring(0, 2);
		String v_situacao_executando = paramSituacaoExecutando.getVlrTexto()
				.substring(0, 2);

		String[] codigos = new String[] { v_situacao_liberado,
				v_situacao_executando };

		List<AelItemSolicitacaoExames> fichaAnestesia = getExamesFacade()
				.listarItemSolicitacaoExamePorSeqCirurgiaSitCodigo(seqCirurgia,
						codigos);

		if (fichaAnestesia == null || fichaAnestesia.isEmpty()) {
			botaoExameAnatomopatologico = Boolean.FALSE;
		} else {
			botaoExameAnatomopatologico = Boolean.TRUE;
		}

		return botaoExameAnatomopatologico;
	}

	public Boolean habilitarBotaoDocAssinado(Integer seqCirurgia) {

		Boolean prntolAdmin = recuperarGlobalAipfPrntolAdmin();

		Boolean botaoDocAssinado = null;
		Boolean v_tem_doc_ass = null;

		if (prntolAdmin == null) {
//			atribuirContextoSessao(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN,
//					Boolean.FALSE);// prntol_admin = "N"
			prntolAdmin = Boolean.FALSE;
		}

		List<AghDocumentosAssinados> docAssinado = getAghuFacade()
				.listarDocAssPorSeqCirurgiaDocAssinadoIsNotNull(seqCirurgia);

		if (docAssinado == null || docAssinado.isEmpty()) {
			v_tem_doc_ass = Boolean.FALSE;
		}else{
			v_tem_doc_ass = Boolean.TRUE;
		}

		if (prntolAdmin == Boolean.FALSE) {
			if (v_tem_doc_ass == Boolean.TRUE) {
				botaoDocAssinado = Boolean.TRUE;
			} else {
				botaoDocAssinado = Boolean.FALSE;
			}
		}

		return botaoDocAssinado;
	}
	
	/**
	 * RN3
	 * @ORADB FUNCAO MBCC_GET_MOTIVO_CANC
	 * @author Lilian
	 * @param seq
	 */	
	public MbcExtratoCirurgia buscarMotivoCancelCirurgia(Integer seq) {

		MbcExtratoCirurgia extratoCirurgia = getBlocoCirurgicoFacade().buscarMotivoCancelCirurgia(seq);	


		if (extratoCirurgia != null) {			 
			
			extratoCirurgia.setDescricaoMotivoCancelamentoEditado(
					"Cirurgia cancelada em " + DateUtil.dataToString(extratoCirurgia.getCriadoEm(), "dd/MM/yyyy HH:mm")  + 
					" por " + extratoCirurgia.getServidor().getPessoaFisica().getNome() + "\n");
				
			if (extratoCirurgia.getCirurgia() != null && extratoCirurgia.getCirurgia().getMotivoCancelamento() != null && extratoCirurgia.getCirurgia().getMotivoCancelamento().getDescricao() != null) {

				String descricaoMinusculo = getAmbulatorioFacade().obterDescricaoCidCapitalizada(extratoCirurgia.getCirurgia().getMotivoCancelamento().getDescricao(),CapitalizeEnum.PRIMEIRA);

				extratoCirurgia.setDescricaoMotivoCancelamentoEditado(
						extratoCirurgia.getDescricaoMotivoCancelamentoEditado() + "Motivo: " + descricaoMinusculo);

				if (extratoCirurgia.getCirurgia().getValorValidoCanc() != null) {

					if (extratoCirurgia.getCirurgia().getValorValidoCanc().getValor() != null){

						extratoCirurgia.setDescricaoMotivoCancelamentoEditado(extratoCirurgia.getDescricaoMotivoCancelamentoEditado() + ": " + 
								extratoCirurgia.getCirurgia().getValorValidoCanc().getValor());
					}
				}
				
				if (extratoCirurgia.getCirurgia().getValorValidoCanc() != null) {					
					if (extratoCirurgia.getCirurgia().getValorValidoCanc().getValor() != null){	
						extratoCirurgia.setDescricaoMotivoCancelamentoEditado(extratoCirurgia.getDescricaoMotivoCancelamentoEditado() + ": " + 
						extratoCirurgia.getCirurgia().getValorValidoCanc().getValor());	
					}	
				}

				if (extratoCirurgia.getCirurgia().getComplementoCanc() != null) {

					if (extratoCirurgia.getCirurgia().getQuestao() != null && extratoCirurgia.getCirurgia().getQuestao().getDescricao() == null){

						extratoCirurgia.setDescricaoMotivoCancelamentoEditado(extratoCirurgia.getDescricaoMotivoCancelamentoEditado() + ": ");
					}else{
						extratoCirurgia.setDescricaoMotivoCancelamentoEditado(extratoCirurgia.getDescricaoMotivoCancelamentoEditado() + " ");
					}

					extratoCirurgia.setDescricaoMotivoCancelamentoEditado(extratoCirurgia.getDescricaoMotivoCancelamentoEditado() + 
							extratoCirurgia.getCirurgia().getComplementoCanc());

				}
			}	
		}

		return extratoCirurgia;
	}

	/*
	 * RN7 - Executar ao clicar no botão Descricao
	 */

	/*
	 * protected String executarBotaoDescricao(Integer seqCirurgia) throws
	 * ApplicationBusinessException{ DominioTipoDocumento tipo =
	 * DominioTipoDocumento.DC;
	 * 
	 * Boolean certifAssDoc =
	 * getConsultarNotasPolRN().verificarCertificadoAssinado(seqCirurgia, tipo);
	 * 
	 * if (certifAssDoc == Boolean.TRUE){
	 * this.chamarDocCertifCirurg(seqCirurgia, tipo); return
	 * "documentoAssinado"; }else{ //this.imprimirDescricao(seqCirurgia); return
	 * "documento"; } }
	 */

	public Boolean verificarSeDocumentoDescricaoCirugiaAssinado(
			Integer seqCirurgia) throws ApplicationBusinessException {
		DominioTipoDocumento tipo = DominioTipoDocumento.DC;

		Boolean certifAssDoc = getConsultarNotasPolRN()
				.verificarCertificadoAssinado(seqCirurgia, tipo);

		return certifAssDoc;
	}

	/**
	 * RN9 ORADB: PROCEDURE P_CHAMA_DOC_CERTIF_CIRG
	 * 
	 * @param seqCirurgia, tipo
	 * @author heliz
	 */
	private Integer chamarDocCertifCirurg(Integer seqCirurgia,
			DominioTipoDocumento tipo) {

		Integer v_dov_seq = this.getConsultarNotasPolRN().buscarVersaoSeqDoc(
				seqCirurgia, tipo);

		return v_dov_seq;
	}

	public Integer chamarDocCertifCirurg(Integer seqCirurgia) {
		return chamarDocCertifCirurg(seqCirurgia, DominioTipoDocumento.DC);
	}

	/**
	 * RN12 ORADB: IMPRESSAO_DESCRICAO
	 * 
	 * 0 --> seqEnvioRelatorio --> Pode ser seqCirurgia ou PdtDescricaoSeq
	 * 1 --> seq2EnvioRelatorio --> SeqP da MbcDescricaoCirurgica
	 * 2 --> nomeRelatorio
	 * 3 --> comModal
	 * 4 --> exibeRelatorioPdtrDescricao
	 * 5 --> exibeRelatorioMbcrDescrCirurgica
	 * 6 --> lista de PdtDescricao
	 * 7 --> lista de MbcDescricaoCirurgica
	 * 8 --> Permite Impressao
	 *
	 * 
	 * @author heliz
	 * @param seqCirurgia
	 * @throws ApplicationBusinessException
	 */
	public Object[] imprimirDescricao(Integer seqCirurgia)
			throws ApplicationBusinessException {
		Object [] retorno = null;
		Boolean prntolAdmin = recuperarGlobalAipfPrntolAdmin();

		if (prntolAdmin == null) {
//			atribuirContextoSessao(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN,
//					Boolean.FALSE);// prntol_admin = "N"
			prntolAdmin = Boolean.FALSE;
		}

		DominioSituacaoDescricao[] situacaoDescricao = new DominioSituacaoDescricao[] {
				DominioSituacaoDescricao.PRE, DominioSituacaoDescricao.DEF };

		List<MbcDescricaoCirurgica> descricaoCirurgica = getBlocoCirurgicoFacade()
				.listarDescricaoCirurgicaPorSeqCirurgiaSituacao(seqCirurgia, DominioSituacaoDescricaoCirurgia.CON);
		
		if (Boolean.FALSE.equals(prntolAdmin)) {
			if (descricaoCirurgica == null || descricaoCirurgica.isEmpty()) {
				List<PdtDescricao> descricaoPdt = getBlocoCirurgicoProcDiagTerapFacade()
				.listarDescricaoPorSeqCirurgiaSituacao(seqCirurgia,
						situacaoDescricao, PdtDescricao.Fields.SEQ);
			
				if (descricaoPdt != null && !descricaoPdt.isEmpty()) {
					retorno = setRetornoRelatorioPdt(descricaoPdt);
				}
			} else {
				retorno = setRetornoRelatorioMbcr(descricaoCirurgica, seqCirurgia);
			}
		}
		
		return retorno;
	}

	public Boolean getUsuarioLogadoPerfilAdm07(String vPerfilAdm07) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
			return getICascaFacade().usuarioTemPerfil(servidorLogado.getUsuario(), vPerfilAdm07);
	}

	private Object[] setRetornoRelatorioMbcr(
			List<MbcDescricaoCirurgica> descricaoCirurgica, Integer crgSeq) {
		Object [] ret = new Object[10];
		if(descricaoCirurgica != null && descricaoCirurgica.size() == 1){
			ret[0] = crgSeq;
			ret[1] = descricaoCirurgica.get(0).getId().getSeqp();
			ret[2] = "mbcrDescrCirurgica";
			ret[3] = Boolean.FALSE;
			ret[4] = Boolean.FALSE;
			ret[5] = Boolean.TRUE;
			ret[6] = null;
			ret[7] = null;
		}else if(descricaoCirurgica != null && descricaoCirurgica.size() > 1){
			ret[0] = crgSeq;
			ret[1] = null;
			ret[2] = "mbcrDescrCirurgica";
			ret[3] = Boolean.TRUE;
			ret[4] = Boolean.FALSE;
			ret[5] = Boolean.TRUE;
			ret[6] = null;
			ret[7] = processaDescricaoComNomeResponsavelMbc(descricaoCirurgica);
		}else{ //if lista == null
			ret[3] = null;
		}
		return ret;
	}

	private Object[] setRetornoRelatorioPdt(List<PdtDescricao> descricaoPdt) {
		Object [] ret = new Object[10];
		if(descricaoPdt != null && descricaoPdt.size() == 1 && descricaoPdt.size() != 0){
			ret[0] = descricaoPdt.get(0).getSeq();
			ret[1] = null;
			ret[2] = "pdtrDescricao";
			ret[3] = Boolean.FALSE;
			ret[4] = Boolean.TRUE;
			ret[5] = Boolean.FALSE;
			ret[6] = null;
			ret[7] = null;
		}else if(descricaoPdt != null && descricaoPdt.size() > 1 && descricaoPdt.size() != 0){
			ret[0] = null;
			ret[1] = null;
			ret[2] = "pdtrDescricao";
			ret[3] = Boolean.TRUE;
			ret[4] = Boolean.TRUE;
			ret[5] = Boolean.FALSE;
			ret[6] = processaDescricaoComNomeResponsavel(descricaoPdt);
			ret[7] = null;
		}else{ //if lista == null
			ret[3] = null;
		}
		
		return ret;
	}
	
	public List<MbcDescricaoCirurgica> processaDescricaoComNomeResponsavelMbc(
			List<MbcDescricaoCirurgica> descricaoCirurgica) {
		for(MbcDescricaoCirurgica desc : descricaoCirurgica){
			String nomeResp = getBlocoCirurgicoFacade().buscarProfDescricoes(desc.getId().getCrgSeq(), desc.getId().getSeqp()).get(0).getServidorProf().getPessoaFisica().getNome();
			desc.setNomeResponsavel(capitalizaTodas(nomeResp));
			MbcProcDescricoes proc = getBlocoCirurgicoFacade().buscarProcDescricoes(desc.getId().getCrgSeq(), desc.getId().getSeqp());
			desc.setDescricaoProcCirurgico(proc.getProcedimentoCirurgico().getDescricao() + " " + (proc.getComplemento()!=null?proc.getComplemento():""));
			desc.setDescricaoProcCirurgico(capitalizaTodas(desc.getDescricaoProcCirurgico()));
		}
		return descricaoCirurgica;
	}

	public List<PdtDescricao> processaDescricaoComNomeResponsavel(
			List<PdtDescricao> descricaoPdt) {
		for(PdtDescricao desc : descricaoPdt){
			String pesFisica = getBlocoCirurgicoProcDiagTerapFacade().obterNomePessoaPdtProfByPdtDescricao(desc.getSeq(), desc.getMbcCirurgias().getSeq());
			desc.setNomeResponsavel(capitalizaTodas(pesFisica));
			PdtProcDiagTerap pdtProg = getBlocoCirurgicoProcDiagTerapFacade().obterPdtProcDiagTerap(desc.getSeq());
			if(pdtProg != null && pdtProg.getDescricao() != null){
				desc.setDescricaoPdtProgDiag(capitalizaTodas(pdtProg.getDescricao()));
			}
		}
		return descricaoPdt;
	}
	
	/*
	 * RN13 - Executar ao clicar no botão Ato Anestésico
	 */

	/*
	 * protected String executarBotaoAtoAnestesico(Integer seqCirurgia) throws
	 * ApplicationBusinessException{
	 * 
	 * String pendente = "V"; List<MbcFichaAnestesias> fichaAnestesia =
	 * getMbcFichaAnestesiasDAO
	 * ().listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(seqCirurgia,
	 * pendente);
	 * 
	 * DominioTipoDocumento tipo = DominioTipoDocumento.FA;
	 * 
	 * Boolean certifAssDoc =
	 * getConsultarNotasPolRN().verificarCertificadoAssinado(seqCirurgia, tipo);
	 * 
	 * 
	 * if (fichaAnestesia != null){ if (certifAssDoc == Boolean.TRUE){
	 * this.chamarDocCertifFicha(seqCirurgia, tipo); }else{
	 * this.impressaoFicha(seqCirurgia, tipo); } } }
	 */

	public Boolean verificarSeDocumentoAtoAnestesicoAssinado(
			Integer seqCirurgia) throws ApplicationBusinessException {
		//String pendente = "V";
		DominioIndPendenteAmbulatorio pendente = DominioIndPendenteAmbulatorio.V;
		List<MbcFichaAnestesias> fichaAnestesia = getBlocoCirurgicoFacade()
				.listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(
						seqCirurgia, pendente);

		DominioTipoDocumento tipo = DominioTipoDocumento.FA;

		Boolean certifAssDoc = false;

		if (fichaAnestesia != null && !fichaAnestesia.isEmpty()) {
			certifAssDoc = getConsultarNotasPolRN()
					.verificarCertificadoAssinado(seqCirurgia, tipo);

			return certifAssDoc;
		}

		return certifAssDoc;
	}

	/**
	 * RN14 ORADB: PROCEDURE P_CHAMA_DOC_CERTIF_FICHA
	 * 
	 * @param seqCirurgia, tipo
	 * @author heliz
	 */
	private Integer chamarDocCertifFicha(Integer seqCirurgia,
			DominioTipoDocumento tipo) {

		Integer vDovSeq = this.getConsultarNotasPolRN().buscarVersaoSeqDoc(
				seqCirurgia, tipo);

		/*
		 * if(v_fiq_seq != null){ //TODO: RN11 P_VISUALIZA_PDF_CERTIF }
		 */

		return vDovSeq;
	}

	public Integer chamarDocCertifFicha(Integer seqCirurgia) {
		return chamarDocCertifFicha(seqCirurgia, DominioTipoDocumento.FA);
	}

	/**
	 * RN15 ORADB: IMPRESSAO_FICHA
	 * 
	 * @author heliz
	 * @throws ApplicationBusinessException
	 * @param seqCirurgia
	 * @param seqpAnestesico 
	 * @param soeSeqAnestesico 
	 */
	public Object[] processarRelatorioAtosAnestesicos(Integer seqCirurgia, Integer soeSeqAnestesico, Short seqpAnestesico, String idSessao)
			throws ApplicationBusinessException {
		//Boolean prntolAdmin = (Boolean) obterContextoSessao("AIPF_PRNTOL_ADMIN"); // variável
																									// global
		Boolean prntolAdmin = false;
		
//		if (prntolAdmin == null) {
////			atribuirContextoSessao(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN,
////					Boolean.FALSE);// prntol_admin = "N"
//			prntolAdmin = Boolean.FALSE;
//		}

		//String pendente = "V";
		DominioIndPendenteAmbulatorio pendente = DominioIndPendenteAmbulatorio.V;
		List<MbcFichaAnestesias> fichaAnestesia = null;
		if(seqCirurgia == null && (soeSeqAnestesico != null && seqpAnestesico != null)){
			fichaAnestesia = getBlocoCirurgicoFacade().listarFichasAnestesiasPorItemSolicExame(soeSeqAnestesico, seqpAnestesico, pendente, Boolean.TRUE);
		}else{
			fichaAnestesia = getBlocoCirurgicoFacade()
			.listarFichasAnestesiasPorSeqCirurgiaPendenteDthrMvtoNull(
					seqCirurgia, pendente);
		}
		

		if (!prntolAdmin) {
			if (fichaAnestesia != null && !fichaAnestesia.isEmpty()) {
				// Chama report jasper MBCR_FICHA_JASPER
				// Se tem perfil do SAMIS, libera impressão, senão, desabilita
				// Gera dados da temporário da matriz de Fármacos

				Long ficSeq = fichaAnestesia.get(0).getSeq();

				String vSessao = gerarFarmacos(ficSeq, idSessao);

				Object[] retorno = new Object[3];
				retorno [0] = ficSeq; //seqMbcFichaAnestesia
				retorno [1] = vSessao; //vSessao
				
				return retorno;
			}
			throw new ApplicationBusinessException(CirurgiasInternacaoPOLRNExceptionCode.ERRO_GERAR_RELATORIO_ATOS_ANESTESICOS_FICHA_NULA, "AIPF_PRNTOL_ADMIN");
		}
		throw new ApplicationBusinessException(CirurgiasInternacaoPOLRNExceptionCode.ERRO_GERAR_RELATORIO_ATOS_ANESTESICOS_VGLOBAL, "AIPF_PRNTOL_ADMIN");
	}

	/**
	 * RN16 ORADB: MBCC_GERA_FARMACOS
	 * 
	 * @author heliz
	 * @throws ApplicationBusinessException
	 * @param seqMbcFichaAnestesia
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String gerarFarmacos(Long seqMbcFichaAnestesia, String vSessao) {
		
		Boolean vAberto = Boolean.FALSE;
		Boolean achouCelula = Boolean.FALSE;
		Date vHorarioAtual = null;
		Integer vOrdem = 0;
		BigDecimal vTotalGas = null;
		Date vInicioGas = null;
		String vTotFormatado;
		String vCelula = null;
		//Date sysdate = new GregorianCalendar().getTime();
		Short vTempoDecorrido;
		
		//Limpa dados anteriores a 24 horas
		getBlocoCirurgicoFacade().removerMbcTmpFichaFarmacoAnteriores(1, Boolean.TRUE);
		
		//Busca a sessão
		//String vSessao = ((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
		//TODOMIGRACAO
	//	String vSessao = null;

		// Busca o início da anestesia
		Date vInicioAnest = recuperarDtHrOcorrenciaFichaAvento(seqMbcFichaAnestesia, DominioOcorrenciaFichaEvento.I);

		// Busca o fim da anestesia
		Date vFimAnest = recuperarDtHrOcorrenciaFichaAvento(seqMbcFichaAnestesia, DominioOcorrenciaFichaEvento.F);

		//Lista que representa os registros que devem ser incluídos em MbcTmpFichaFarmaco
		List<MbcTmpFichaFarmaco> mbcTmpsFichas = new ArrayList<MbcTmpFichaFarmaco>();
		
		
		//---- Busca informações do gás----
		List<MbcFichaGas> fichaGas = getBlocoCirurgicoFacade().listarMbcFichaGasesComMaterial(seqMbcFichaAnestesia);
		
		for (MbcFichaGas rGas : fichaGas) {
			vAberto = Boolean.FALSE;
			achouCelula = Boolean.FALSE;
			Integer vAtual = rGas.getSeq();
			vHorarioAtual = null;
			vOrdem = vOrdem + 1;
			
			// Início calcula total do gás
			List<MbcOcorrenciaFichaGas> ocorrenciaFichaGas = listarMbcOcorrenciaFichaGas(rGas
					.getSeq());

			for (MbcOcorrenciaFichaGas rCal : ocorrenciaFichaGas) {
				if (DominioTipoOcorrenciaFichaFarmaco.I.equals(rCal.getTipo())) {
					vInicioGas = rCal.getDthrOcorrencia();
				} else if (DominioTipoOcorrenciaFichaFarmaco.F.equals(rCal.getTipo()) && vInicioGas != null) {
					vTotalGas = ((vTotalGas != null) ? vTotalGas : BigDecimal.ZERO).add(
									new BigDecimal(rCal.getDthrOcorrencia().getTime() - vInicioGas.getTime())
								);
					vInicioGas = null;
				}
			}
			if (vInicioGas != null) {
				vTotalGas = ((vTotalGas != null) ? vTotalGas : BigDecimal.ZERO).add(
						new BigDecimal(vFimAnest.getTime() - vInicioGas.getTime())
					);
			}
			vTotFormatado = formataHoras(vTotalGas);
			// Fim calcula total do gás

			
			// Inclui uma cédula para cada coluna da linha
			List<OcorrenciaFichaFarmacoGasesVO> cOcorrencia = listarOcorrenciasFichaFarmacoGases(rGas
					.getMbcFichaAnestesias().getSeq());
			
			for (OcorrenciaFichaFarmacoGasesVO rOco : cOcorrencia) {

				// Verifica se já existe informação para esta célula
				if (!rOco.getDthrOcorrencia().equals(vHorarioAtual) || !achouCelula) {

					// Monta a informação a ser incluída na célula
					vCelula = null;
					vHorarioAtual = rOco.getDthrOcorrencia();
					if (rOco.getFfaSeq().equals(rGas.getSeq())) {

						// Monta célula gás
						if (DominioTipoOcorrenciaFichaFarmaco.I.equals(rOco.getTipoOcorrencia())) {
							vCelula = "(" + new BigDecimal(rOco.getFluxo()).setScale(0, RoundingMode.FLOOR) + "L/min";
						} else if (DominioTipoOcorrenciaFichaFarmaco.S.equals(rOco.getTipoOcorrencia())) {
							vCelula = new BigDecimal(rOco.getFluxo()).setScale(0, RoundingMode.FLOOR) + "L/min";
						} else if (DominioTipoOcorrenciaFichaFarmaco.F.equals(rOco.getTipoOcorrencia())) {
							vCelula = HIFEN_HIFEN_PAR;
						}
						// Fim monta célula gás

						achouCelula = Boolean.TRUE;

						if (DominioTipoOcorrenciaFichaFarmaco.I.equals(rOco.getTipoOcorrencia())) {
							vAberto = Boolean.TRUE;
						} else if (DominioTipoOcorrenciaFichaFarmaco.F.equals(rOco.getTipoOcorrencia())) {
							vAberto = Boolean.FALSE;
						}
					} else {
						// Verifica se é uma célula intermediária
						if (rOco.getFfaSeq().equals(vAtual)){
							if (vAberto) {
								vCelula = "---";
							}
						}
					}
				}

				// Verifica se horário já existe para esta linha
				Integer cTemp = verificarSeMbcTmpEstaNaLista(mbcTmpsFichas,
						vSessao, rGas.getMbcFichaAnestesias().getSeq(),
						rGas.getSeq(), rOco.getDthrOcorrencia());

				if (cTemp == null) {
					// Inclui o horário para a linha
					vTempoDecorrido = new BigDecimal((rOco.getDthrOcorrencia().getTime() - vInicioAnest.getTime())/1000 / 60).shortValue();
					MbcTmpFichaFarmaco mbcTmpFichaFarmaco = new MbcTmpFichaFarmaco();
					mbcTmpFichaFarmaco.setId(new MbcTmpFichaFarmacoId());
					mbcTmpFichaFarmaco.getId().setIdSessao(vSessao);
					mbcTmpFichaFarmaco.getId().setFicSeq(Integer.valueOf(rGas.getMbcFichaAnestesias().getSeq().toString()));
					mbcTmpFichaFarmaco.getId().setFfaSeq(rGas.getSeq());
					mbcTmpFichaFarmaco.getId().setDescMedicamento(capitalizaTodas(rGas.getScoMaterial().getNome()));
					mbcTmpFichaFarmaco.getId().setDoseTotal(vTotFormatado);
					mbcTmpFichaFarmaco.getId().setDthrOcorrencia(rOco.getDthrOcorrencia());
					mbcTmpFichaFarmaco.getId().setTempoDecorrido(vTempoDecorrido);
					mbcTmpFichaFarmaco.getId().setCelula(vCelula);
					mbcTmpFichaFarmaco.getId().setOrdem(vOrdem);
					mbcTmpFichaFarmaco.getId().setCriadoEm(new Date());
					mbcTmpsFichas.add(mbcTmpFichaFarmaco);
				}else{
					//Altera conteúdo de horário já existente para a linha
					if(achouCelula){
						MbcTmpFichaFarmaco mbcTmp = mbcTmpsFichas.get(cTemp);
						mbcTmp.getId().setCelula(vCelula);
						mbcTmp.getId().setCriadoEm(new Date());
					}
				}
				achouCelula = Boolean.FALSE;
			}
		}
		
		
		//---- Busca informações do Fármaco----
		
		 
		List<FichaFarmacoVO> fichaFarmacoVO = buscarInformacoesFarmacos(seqMbcFichaAnestesia);
	
		for (FichaFarmacoVO r1 : fichaFarmacoVO) {//TODO:conferir
			vAberto = Boolean.FALSE;
			achouCelula = Boolean.FALSE;
			Integer vAtual = r1.getSeq();
			vHorarioAtual = null;
			vOrdem = vOrdem + 1;

			// Inclui uma cédula para cada coluna da linha
			List<OcorrenciaFichaFarmacoGasesVO> cOcorrencia = listarOcorrenciasFichaFarmacoGases(r1.getFicSeq());
			
			for (OcorrenciaFichaFarmacoGasesVO rOco : cOcorrencia) {

				// Verifica se já existe informação para esta célula
				if (!rOco.getDthrOcorrencia().equals(vHorarioAtual) || !achouCelula) {

					// Monta a informação a ser incluída na célula
					vCelula = null;
					vHorarioAtual = rOco.getDthrOcorrencia();
					if (rOco.getFfaSeq().equals(r1.getSeq())) {
						vCelula = mbccGetCelFarmaco(rOco.getSeqOcorrencia());
						achouCelula = Boolean.TRUE;

						if (DominioTipoOcorrenciaFichaFarmaco.I.equals(rOco.getTipoOcorrencia())
								&& (r1.getInfusao() || "IN".equals(r1.getVadSigla()))) {
							vAberto = Boolean.TRUE;
						} else if (DominioTipoOcorrenciaFichaFarmaco.F.equals(rOco.getTipoOcorrencia())) {
							vAberto = Boolean.FALSE;
						}
					}else{
						// Verifica se é uma célula intermediária
						if (!rOco.getFfaSeq().equals(vAtual)) {
							if (vAberto) {
								vCelula = "---";
							}
						}
					}
				}

				// Verifica se horário já existe para esta linha
				Integer cTemp = verificarSeMbcTmpEstaNaLista(mbcTmpsFichas,
						vSessao, r1.getFicSeq(), r1.getSeq(), rOco.getDthrOcorrencia());
				
				if (cTemp == null) {
					// Inclui o horário para a linha
					vTempoDecorrido = new BigDecimal((rOco.getDthrOcorrencia().getTime() - vInicioAnest.getTime())/1000 / 60).shortValue();
					MbcTmpFichaFarmaco mbcTmpFichaFarmaco = new MbcTmpFichaFarmaco();
					mbcTmpFichaFarmaco.setId(new MbcTmpFichaFarmacoId());
					mbcTmpFichaFarmaco.getId().setIdSessao(vSessao);
					mbcTmpFichaFarmaco.getId().setFicSeq(
							Integer.valueOf(r1.getFicSeq().toString())
					);
					mbcTmpFichaFarmaco.getId().setFfaSeq(r1.getSeq());
					mbcTmpFichaFarmaco.getId().setDescMedicamento(r1.getMedicamento());
					mbcTmpFichaFarmaco.getId().setDoseTotal(r1.getDoseTotal()); 
					mbcTmpFichaFarmaco.getId().setDthrOcorrencia(rOco.getDthrOcorrencia());
					mbcTmpFichaFarmaco.getId().setTempoDecorrido(vTempoDecorrido);
					mbcTmpFichaFarmaco.getId().setCelula(vCelula);
					mbcTmpFichaFarmaco.getId().setOrdem(vOrdem);
					mbcTmpFichaFarmaco.getId().setCriadoEm(new Date());
					mbcTmpsFichas.add(mbcTmpFichaFarmaco);
				}else{
					//Altera conteúdo de horário já existente para a linha
					if(achouCelula){
						MbcTmpFichaFarmaco mbcTmp = mbcTmpsFichas.get(cTemp);
						mbcTmp.getId().setCelula(vCelula);
						mbcTmp.getId().setCriadoEm(new Date());
					}
				}
				achouCelula = Boolean.FALSE;
			}
		}
		
		//---- Monitorização ----
		List<Object[]> tipoItemMonitorizacao = getBlocoCirurgicoFacade().pesquisarTipoItemMonitorizacaoComMedicao(seqMbcFichaAnestesia);
		vOrdem = vOrdem + 1;
		
		for (Object[] rTip : tipoItemMonitorizacao) {
			List<MbcFichaMedMonitorizacao> fichaMedMonitorizacao = getBlocoCirurgicoFacade().pesquisarFichaMedMonitorizacaoComMedicao(
					seqMbcFichaAnestesia, (Short) rTip[1]);
			
			for(MbcFichaMedMonitorizacao rMon : fichaMedMonitorizacao){
				vTempoDecorrido = new BigDecimal((rMon.getDthrMedicao().getTime() - vInicioAnest.getTime())/1000 / 60).shortValue();
				NumberFormat formatter = new DecimalFormat("#########0.##");
				vCelula = formatter.format(rMon.getMedicao());
				
				MbcTmpFichaFarmaco mbcTmpFichaFarmaco = new MbcTmpFichaFarmaco();
				mbcTmpFichaFarmaco.setId(new MbcTmpFichaFarmacoId());
				mbcTmpFichaFarmaco.getId().setIdSessao(vSessao);
				mbcTmpFichaFarmaco.getId().setFicSeq(
						Integer.valueOf(rMon.getMbcFichaMonitorizacao().getMbcFichaAnestesias().getSeq().toString()));
				mbcTmpFichaFarmaco.getId().setFfaSeq(rMon.getMbcFichaMonitorizacao().getMbcItemMonitorizacao().getSeq().intValue());
				mbcTmpFichaFarmaco.getId().setDescMedicamento(
						rMon.getMbcFichaMonitorizacao()
								.getMbcItemMonitorizacao()
								.getMbcTipoItemMonitorizacao()
								.getNomeReduzido());
				mbcTmpFichaFarmaco.getId().setDoseTotal(null); 
				mbcTmpFichaFarmaco.getId().setDthrOcorrencia(rMon.getDthrMedicao());
				mbcTmpFichaFarmaco.getId().setTempoDecorrido(vTempoDecorrido);
				mbcTmpFichaFarmaco.getId().setCelula(vCelula);
				mbcTmpFichaFarmaco.getId().setOrdem(vOrdem);
				mbcTmpFichaFarmaco.getId().setCriadoEm(new Date());
				Integer mbcTmpExiste = verificarSeMbcTmpEstaNaLista(
						mbcTmpsFichas,	vSessao, 
						rMon.getMbcFichaMonitorizacao().getMbcFichaAnestesias().getSeq(), 
						rMon.getMbcFichaMonitorizacao().getMbcItemMonitorizacao().getSeq().intValue(), 
						rMon.getDthrMedicao());
				if(mbcTmpExiste == null){
					mbcTmpsFichas.add(mbcTmpFichaFarmaco);
				}
			}
			
		}
		
		insereAtualizaMbcTmpFichaFarmaco(mbcTmpsFichas);
		
		return vSessao;
	}

	private List<FichaFarmacoVO> buscarInformacoesFarmacos(
			Long seqMbcFichaAnestesia) {
		List<FichaFarmacoVO> fichaFarmacoVO = new ArrayList<FichaFarmacoVO>();
		List<MbcFichaFarmaco> fichaFarmaco = getBlocoCirurgicoFacade().listarFichaFarmacoPorFicSeq(seqMbcFichaAnestesia);
		
		for (MbcFichaFarmaco ff : fichaFarmaco) {
			FichaFarmacoVO vo = new FichaFarmacoVO();
			vo.setFicSeq(ff.getMbcFichaAnestesias().getSeq());
			vo.setSeq(ff.getSeq());
			vo.setInfusao(ff.getInfusao());
			vo.setVadSigla(ff.getViaAdministracao().getSigla());
			vo.setMedicamento( getProntuarioOnlineFacade().mbccGetFarmaco(ff.getSeq()));
			vo.setDoseTotal(formataDoseTotal(
					ff.getDoseTotalAdministrada(),
					(ff.getFormaDosagem().getUnidadeMedidaMedicas() != null ? ff.getFormaDosagem().getUnidadeMedidaMedicas().getSeq() : null),
					ff.getMedicamento().getTipoApresentacaoMedicamento().getDescricao(),
					(ff.getFormaDosagem().getUnidadeMedidaMedicas() != null ? ff.getFormaDosagem().getUnidadeMedidaMedicas().getDescricao() : null))); 

			fichaFarmacoVO.add(vo);
		}
		
		CoreUtil.ordenarLista(fichaFarmacoVO, "medicamento", Boolean.TRUE);
		return fichaFarmacoVO;
	}

	private void insereAtualizaMbcTmpFichaFarmaco(
			List<MbcTmpFichaFarmaco> mbcTmpsFichas) {
		for(MbcTmpFichaFarmaco reg : mbcTmpsFichas){
			getBlocoCirurgicoFacade().persistirMbcTmpFichaFarmaco(reg);
		}
	}

	private Integer verificarSeMbcTmpEstaNaLista(
			List<MbcTmpFichaFarmaco> mbcTmpsFichas, String vSessao,
			Long ficSeq, Integer ffaSeq, Date dthrOcorrencia) {

		for(int i =0; i < mbcTmpsFichas.size(); i++){
			MbcTmpFichaFarmaco reg = mbcTmpsFichas.get(i);
			
			if(reg.getId().getIdSessao().equals(vSessao)
					&& reg.getId().getFicSeq().equals(Integer.parseInt(ficSeq.toString()))
					&& reg.getId().getFfaSeq().equals(ffaSeq)
					&& reg.getId().getDthrOcorrencia().equals(dthrOcorrencia)
			){
				return i;
			}
		}
		return null;
	}

	/**
	 * @ORADB MBCC_GET_CEL_FARMACO
	 * @param seqMbcOcorrenciaFichaFarmaco
	 * @author heliz
	 */
	private String mbccGetCelFarmaco(Integer seqMbcOcorrenciaFichaFarmaco) {
		
		Boolean vAchou = null;
		String vResult = null;
		Double vMcgKgMin;
		
		MbcOcorrenciaFichaFarmaco rOco = obterMbcOcorrenciaFichaFarmacoBySeq(seqMbcOcorrenciaFichaFarmaco);
		
		//Verifica se o fármaco possui outras vias, diferente de inalatório
		String vad_sigla = "IN";
		Boolean rVias = verificarSeMedicamentoPossuiViaSiglaDiferente(vad_sigla, rOco.getMbcFichaFarmaco().getMedicamento().getMatCodigo());
		
		if (!rVias) {
			vAchou = Boolean.FALSE;
		}
		
		//Fármaco em infusão
		if(Boolean.TRUE.equals(rOco.getMbcFichaFarmaco().getInfusao())){
			
			//Busca o peso do paciente
			List<MbcFichaPaciente> mbcFichaPaciente = pesquisarMbcFichasPacientes(rOco.getMbcFichaFarmaco().getMbcFichaAnestesias().getSeq());
			
			Double vPeso = mbcFichaPaciente.get(0).getPeso();
			
			//Se não tem taxa de infusão retorna concentração alvo
			if(rOco.getTaxaInfusao() == null &&  DominioTipoOcorrenciaFichaFarmaco.I.equals(rOco.getTipoOcorrencia())){
				vResult = "(" + rOco.getMbcFichaFarmaco().getConcentracaoAlvo() + rOco.getMbcFichaFarmaco().getFormaDosagem().getUnidadeMedidaMedicas().getDescricao();
			//Se via intravenosa retorna a taxa de infusão em mcg/kg/min	
			}else if(CoreUtil.in(rOco.getMbcFichaFarmaco().getViaAdministracao().getSigla(),"IV", "EV") && vPeso != null){
				//Na Base algo * null é nulo, então:
				//Calcula mcg/Kg/min
				if(rOco.getMbcFichaFarmaco().getConcentracaoPercent() == null || rOco.getTaxaInfusao() == null){
					vMcgKgMin = null;
				}else{
					vMcgKgMin = (1000 * rOco.getMbcFichaFarmaco().getConcentracaoPercent() * rOco.getTaxaInfusao()) / (vPeso * 6);	
				}
				
				NumberFormat formatter = new DecimalFormat("##0.###");
				String vCalc = (vMcgKgMin != null) ? formatter.format(vMcgKgMin) : "";
				
				if (DominioTipoOcorrenciaFichaFarmaco.S.equals(rOco.getTipoOcorrencia())){
					vResult = vCalc + " MCG/KG/MIN";
				}else if(DominioTipoOcorrenciaFichaFarmaco.I.equals(rOco.getTipoOcorrencia())){
					vResult = /*"(" +*/ vCalc + " MCG/KG/MIN";
				}else if(DominioTipoOcorrenciaFichaFarmaco.F.equals(rOco.getTipoOcorrencia())){
					vResult = HIFEN_HIFEN_PAR;
				}
			//Senão visualiza em ml/hora
			}else{
				if (rOco.getTipoOcorrencia() == DominioTipoOcorrenciaFichaFarmaco.S){
					vResult = rOco.getTaxaInfusao() + " ML/HORA";
				}else if(rOco.getTipoOcorrencia() == DominioTipoOcorrenciaFichaFarmaco.I){
					vResult = "(" + rOco.getTaxaInfusao() + " ML/HORA";
				}else if(rOco.getTipoOcorrencia() == DominioTipoOcorrenciaFichaFarmaco.F){
					vResult = HIFEN_HIFEN_PAR;
				}
			}
		//Fármaco volátil	
		}else if("IN".equals(rOco.getMbcFichaFarmaco().getViaAdministracao().getSigla()) && !vAchou){
			if (rOco.getTipoOcorrencia() == DominioTipoOcorrenciaFichaFarmaco.S){
				vResult = rOco.getConcentracaoVolatil() + "%";
			}else if(rOco.getTipoOcorrencia() == DominioTipoOcorrenciaFichaFarmaco.I){
				vResult = "(" + rOco.getConcentracaoVolatil() + "%";
			}else if(rOco.getTipoOcorrencia() == DominioTipoOcorrenciaFichaFarmaco.F){
				vResult = HIFEN_HIFEN_PAR;
			}
		//Fármaco no Fluído
		}else if(rOco.getMbcFichaFarmaco().getMbcAdministraFichaFluidos() != null){
			NumberFormat formatter = new DecimalFormat("##0.###");
			Double diluicao = ((rOco.getVolumeDiluicaoFluido() == null) ? null
					: rOco.getDose() / rOco.getVolumeDiluicaoFluido());
			
			//Double diluicao = (rOco.getDose() / rOco.getVolumeDiluicaoFluido());
			String diluicaoFormatada = formatter.format(diluicao);//ajustaDecimal(diluicao);
			String unid = ((rOco.getMbcFichaFarmaco().getFormaDosagem().getUnidadeMedidaMedicas().getSeq() == null ? rOco.getMbcFichaFarmaco().getMedicamento().getTipoApresentacaoMedicamento().getDescricao() : rOco.getMbcFichaFarmaco().getFormaDosagem().getUnidadeMedidaMedicas().getDescricao()));
			vResult = diluicaoFormatada + unid + " /ML";
		}else{
			vResult = rOco.getDose() != null ? ajustaDecimal(rOco.getDose()) : "";
		}
	
		return vResult;
	}

	private String ajustaDecimal(Double diluicao) {
		NumberFormat formatter = new DecimalFormat("#########0.##");
		String diluicaoFormatada;
		
		if(Math.floor(diluicao) == 0){
			diluicaoFormatada = "0" + formatter.format(diluicao);
		}else{
			diluicaoFormatada = formatter.format(diluicao);
		}
		
		return diluicaoFormatada;
	}

	private List<MbcFichaPaciente> pesquisarMbcFichasPacientes(Long seqMbcFichaAnestesia) {
		return getBlocoCirurgicoFacade().pesquisarMbcFichasPacientes(seqMbcFichaAnestesia);
	}

	private Boolean verificarSeMedicamentoPossuiViaSiglaDiferente(String sigla,
			Integer matCodigo) {
		return getFarmaciaFacade().verificarSeMedicamentoPossuiViaSiglaDiferente(sigla, matCodigo);
	}

	private MbcOcorrenciaFichaFarmaco obterMbcOcorrenciaFichaFarmacoBySeq(
			Integer seqMbcOcorrenciaFichaFarmaco) {
		return getBlocoCirurgicoFacade().obterMbcOcorrenciaFichaFarmacoBySeq(
				seqMbcOcorrenciaFichaFarmaco);
	}

	private String formataDoseTotal(Double doseTotalAdministrada, Integer ummSeq,
			String tprDescricao, String ummDescricao) {
		StringBuffer doseTotal = new StringBuffer();
		
		/*NumberFormat formatter = new DecimalFormat("#########0.##");
		StringBuffer doseTotalF = new StringBuffer(formatter.format(doseTotalAdministrada));
		
		doseTotal = doseTotalF;*/
		BigDecimal doseFormat = new BigDecimal(doseTotalAdministrada).setScale(2, RoundingMode.FLOOR);
		doseTotal.append(doseFormat.toString().replace(".00", "").replace('.', ','));
		
		if(doseTotalAdministrada != null){
			doseTotal = new StringBuffer(doseTotal).append(' ');
			if(ummSeq == null){
				doseTotal = new StringBuffer(doseTotal).append(tprDescricao);
			}else{
				doseTotal = new StringBuffer(doseTotal).append(ummDescricao);
			}
		}
		
		return doseTotal.toString();
	}

	private List<OcorrenciaFichaFarmacoGasesVO> listarOcorrenciasFichaFarmacoGases(
			Long seqMbcFichaAnestesia) {
		List<MbcOcorrenciaFichaFarmaco> fichasFarmacos = getBlocoCirurgicoFacade()
				.listarMbcFichaFarmacosByMbcFichaAnestesia(seqMbcFichaAnestesia);
		
		List<MbcOcorrenciaFichaGas> fichasGases = getBlocoCirurgicoFacade()
				.listarMbcFichaGasByMbcFichaAnestesia(seqMbcFichaAnestesia);

		List<OcorrenciaFichaFarmacoGasesVO> fichasGasFarmaco = new ArrayList<OcorrenciaFichaFarmacoGasesVO>();

		for (MbcOcorrenciaFichaFarmaco ff : fichasFarmacos) {
			OcorrenciaFichaFarmacoGasesVO vo = new OcorrenciaFichaFarmacoGasesVO();
			vo.setDthrOcorrencia(ff.getDthrOcorrencia());
			vo.setSeqOcorrencia(ff.getSeq());
			vo.setFfaSeq(ff.getMbcFichaFarmaco().getSeq());
			vo.setTipoOcorrencia(ff.getTipoOcorrencia());
			vo.setFluxo(null);

			fichasGasFarmaco.add(vo);
		}

		for (MbcOcorrenciaFichaGas fg : fichasGases) {
			OcorrenciaFichaFarmacoGasesVO vo = new OcorrenciaFichaFarmacoGasesVO();
			vo.setDthrOcorrencia(fg.getDthrOcorrencia());
			vo.setSeqOcorrencia(fg.getSeq());
			vo.setFfaSeq(fg.getMbcFichaGas().getSeq());
			vo.setTipoOcorrencia(fg.getTipo());
			vo.setFluxo(fg.getFluxo());

			fichasGasFarmaco.add(vo);
		}

		CoreUtil.ordenarLista(fichasGasFarmaco, "dthrOcorrencia", Boolean.TRUE);

		return fichasGasFarmaco;
	}

	private String formataHoras(BigDecimal vTotalGas) {
		Long diferencaEmHoras = (vTotalGas.longValue() /1000) / 60 / 60;
		Long minutosRestantes = (vTotalGas.longValue() / 1000)/60 %60;

		return diferencaEmHoras + ":" + minutosRestantes +" hs";
	}

	private List<MbcOcorrenciaFichaGas> listarMbcOcorrenciaFichaGas(
			Integer seqMbcFichaGas) {
		return getBlocoCirurgicoFacade().listarMbcOcorrenciaFichaGas(
				seqMbcFichaGas);
	}

	private Date recuperarDtHrOcorrenciaFichaAvento(Long seqMbcFichaAnestesia, DominioOcorrenciaFichaEvento ocorrenciaFichaEvento) {
		MbcOcorrenciaFichaEvento ocorrFichaEvento = getBlocoCirurgicoFacade()
				.obterMbcOcorrenciaFichaEventoComEventoPrincipal(
						seqMbcFichaAnestesia, ocorrenciaFichaEvento);
		
		return ocorrFichaEvento != null ? ocorrFichaEvento.getDthrOcorrencia() : null;
	}
	
	private String capitalizaTodas(String texto) {
		return getAmbulatorioFacade().obterDescricaoCidCapitalizada(texto,
				CapitalizeEnum.TODAS);
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	private IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected ConsultaNotasPolRN getConsultarNotasPolRN() {
		return consultaNotasPolRN;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade(){
		return this.prontuarioOnlineFacade;
	}

	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade(){
		return this.blocoCirurgicoProcDiagTerapFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
