package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtCidDescON.PdtCidDescONExceptionCode;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtDescricaoRN.DescricaoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgiaPdtSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.PdtCidDesc;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class LiberacaoLaudoPreliminarON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(LiberacaoLaudoPreliminarON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private PdtDadoDescDAO pdtDadoDescDAO;

	@Inject
	private PdtCidDescDAO pdtCidDescDAO;
	
	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade iBlocoCirurgicoProcDiagTerapFacade;

	@EJB
	private DescricaoProcDiagTerapRN descricaoProcDiagTerapRN;

	@EJB
	private PdtDescricaoRN pdtDescricaoRN;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4701435486364683711L;
	
	private static final String DATE_PATTERN_DDMMYYYYHHMM = "dd/MM/yyyy HH:mm";
	
	public enum LiberacaoLaudoPreliminarONExceptionCode implements BusinessExceptionCode {
		ERRO_DESCRICAO_COLISAO_HORARIOS_CIRURGIA, ERRO_DESCRICAO_COLISAO_HORARIOS_PDT, 
		PDT_00138, PDT_00139, PDT_00140, MBC_01096
	}
	
	/**
	 * Método responśavel por Liberar Laudo Preliminar de Descrição de PDT. 
	 * 
	 * @param descricao
	 * @param unfSeq
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void liberarLaudoPreliminar(PdtDescricao descricao, Short unfSeq) throws BaseException {
		Integer ddtSeq = descricao.getSeq();
		
		PdtDadoDescDAO dadoDescDAO = getPdtDadoDescDAO();
		
		PdtDadoDesc dadoDesc = dadoDescDAO.obterPorChavePrimaria(ddtSeq);

		MbcCirurgias cirurgia = descricao.getMbcCirurgias();
		
		MbcSalaCirurgica salaCirurgica = null;
		validarResultadoNormalOuCid(descricao);
		
		if (cirurgia != null) {
			Integer crgSeq = cirurgia.getSeq();
			if(cirurgia.getSalaCirurgica() != null){
				salaCirurgica =  this.getMbcSalaCirurgicaDAO().obterPorChavePrimaria(cirurgia.getSalaCirurgica().getId());
				if (salaCirurgica != null) {
					verificarColisao(unfSeq, cirurgia.getData(), crgSeq, salaCirurgica.getId().getUnfSeq(), salaCirurgica.getId().getSeqp(), 
							dadoDesc.getDthrInicio(), dadoDesc.getDthrFim());
				}	
			}
			
			descricao.setSituacao(DominioSituacaoDescricao.PRE);
			descricao.setDthrExecucao(new Date());
			getPdtDescricaoRN().atualizarDescricao(descricao, true);
			
			// Certificação Digital
			getBlocoCirurgicoFacade().desbloqDocCertificacao(crgSeq, DominioTipoDocumento.PRE);
				
			getDescricaoProcDiagTerapRN().pGeraCertif(crgSeq, unfSeq, DominioTipoDocumento.PRE);			
		}
	}
	
	/**
	 * Verifica colisão de horários com Descrição Cirúrgica / com Descrição de PDT.
	 * 
	 * ORADB: PDTP_VER_COLISAO (form PDTF_DESCRICAO)
	 * 
	 * @param unfSeq
	 * @param dataProced
	 * @param crgSeq
	 * @param sciUnfSeq
	 * @param sciSeqp
	 * @param dthrInicio
	 * @param dthrFim
	 * @throws ApplicationBusinessException 
	 */
	public void verificarColisao(Short unfSeq, Date dataProced, Integer crgSeq,
			Short sciUnfSeq, Short sciSeqp, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException  {
		
		// Verifica colisão de horários com outra descrição cirurgica
		List<DescricaoCirurgiaPdtSalaVO> listaDescricaoCirurgicaSala = 
				getMbcCirurgiasDAO().pesquisarCirurgiaComDescricaoMesmaSala(unfSeq, dataProced, crgSeq, sciUnfSeq, sciSeqp);
		
		formatarEquipe(listaDescricaoCirurgicaSala);
		
		validarColisaoHorarioDescricaoCirurgica(dthrInicio, dthrFim, listaDescricaoCirurgicaSala);
		
		// Verifica colisão de horários com descrição de PDT
		List<DescricaoCirurgiaPdtSalaVO> listaDescricaoPdtSala = 
				getPdtDadoDescDAO().pesquisarCirurgiaComDescricaoPdtMesmaSala(unfSeq, dataProced, crgSeq, sciUnfSeq, sciSeqp);
		
		validarColisaoHorarioDescricaoPdt(dthrInicio, dthrFim, listaDescricaoPdtSala);
		
		Date dataCirurgia = DateUtil.truncaData(dataProced);
		
		validarDthrInicioDthrFim(dthrInicio, dthrFim);
		
		validarDthrInicioDataCirurgia(dthrInicio, dataCirurgia);
		
		validarPrazoDthrInicioDataCirurgia(dthrInicio, dataCirurgia);
		
		validarColisaoHorarioDescricaoCirurgicaDthrFim(dthrInicio, dthrFim, listaDescricaoCirurgicaSala);
		
		validarColisaoHorarioDescricaoPdtDthrFim(dthrInicio, dthrFim, listaDescricaoPdtSala);
		
		/*
		 * Obs.: a validação de tempo mínimo foi separada desse método pois esta
		 * lança um erro não bloqueante, sendo exibida apenas como "warning".
		 */	
	}
	
	/**
	 * Verifica colisão de horários com Descrição Cirúrgica / com Descrição de
	 * PDT. Este método possui todas as validações do método
	 * verificarColisao(...) desta classe exceto a validação para a regra
	 * PDT_00138.
	 * 
	 * @param unfSeq
	 * @param dataProced
	 * @param crgSeq
	 * @param sciUnfSeq
	 * @param sciSeqp
	 * @param dthrInicio
	 * @param dthrFim
	 * @throws ApplicationBusinessException
	 */
	private void verificarColisaoDatasProcedimento(Short unfSeq, Date dataProced, Integer crgSeq,
			Short sciUnfSeq, Short sciSeqp, Date dthrInicio, Date dthrFim) throws ApplicationBusinessException  {
		
		// Verifica colisão de horários com outra descrição cirurgica
		List<DescricaoCirurgiaPdtSalaVO> listaDescricaoCirurgicaSala = 
				getMbcCirurgiasDAO().pesquisarCirurgiaComDescricaoMesmaSala(unfSeq, dataProced, crgSeq, sciUnfSeq, sciSeqp);
		
		formatarEquipe(listaDescricaoCirurgicaSala);
		
		validarColisaoHorarioDescricaoCirurgica(dthrInicio, dthrFim, listaDescricaoCirurgicaSala);
		
		// Verifica colisão de horários com descrição de PDT
		List<DescricaoCirurgiaPdtSalaVO> listaDescricaoPdtSala = 
				getPdtDadoDescDAO().pesquisarCirurgiaComDescricaoPdtMesmaSala(unfSeq, dataProced, crgSeq, sciUnfSeq, sciSeqp);
		
		validarColisaoHorarioDescricaoPdt(dthrInicio, dthrFim, listaDescricaoPdtSala);
		
		Date dataCirurgia = DateUtil.truncaData(dataProced);
		
		validarDthrInicioDataCirurgia(dthrInicio, dataCirurgia);
		
		validarPrazoDthrInicioDataCirurgia(dthrInicio, dataCirurgia);
		
		validarColisaoHorarioDescricaoCirurgicaDthrFim(dthrInicio, dthrFim, listaDescricaoCirurgicaSala);
		
		validarColisaoHorarioDescricaoPdtDthrFim(dthrInicio, dthrFim, listaDescricaoPdtSala);
		
		/*
		 * Obs.: a validação de tempo mínimo foi separada desse método pois esta
		 * lança um erro não bloqueante, sendo exibida apenas como "warning".
		 */	
	}	

	private void validarColisaoHorarioDescricaoCirurgica(Date dthrInicio,
			Date dthrFim,
			List<DescricaoCirurgiaPdtSalaVO> listaDescricaoCirurgicaSala)
			throws ApplicationBusinessException {
		
		for (DescricaoCirurgiaPdtSalaVO descricaoCirurgiaPdtSalaVO : listaDescricaoCirurgicaSala) {
			Date dthrInicioCrg = descricaoCirurgiaPdtSalaVO.getDthrInicioCirg();
			Date dthrFimCrg = descricaoCirurgiaPdtSalaVO.getDthrFimCirg();

			if (((dthrInicio.after(dthrInicioCrg) || dthrInicio
					.equals(dthrInicioCrg)) && dthrInicio.before(dthrFimCrg))
					|| (dthrInicio.before(dthrInicioCrg) && dthrFim.after(dthrFimCrg))) {

				AipPacientes paciente = getPacienteFacade().obterPaciente(
						descricaoCirurgiaPdtSalaVO.getPacCodigo());

				throw new ApplicationBusinessException(
						LiberacaoLaudoPreliminarONExceptionCode.ERRO_DESCRICAO_COLISAO_HORARIOS_CIRURGIA,
						paciente.getNome(), descricaoCirurgiaPdtSalaVO.getSala(),
						DateUtil.obterDataFormatada(dthrInicioCrg, DATE_PATTERN_DDMMYYYYHHMM),
						DateUtil.obterDataFormatada(dthrFimCrg, DATE_PATTERN_DDMMYYYYHHMM));
			}
		}
	}

	private void validarColisaoHorarioDescricaoPdt(Date dthrInicio,
			Date dthrFim, List<DescricaoCirurgiaPdtSalaVO> listaDescricaoPdtSala)
			throws ApplicationBusinessException {
		
		for (DescricaoCirurgiaPdtSalaVO descricaoCirurgiaPdtSalaVO : listaDescricaoPdtSala) {
			Date dthrInicioPdt = descricaoCirurgiaPdtSalaVO.getDthrInicioPdt();
			Date dthrFimPdt = descricaoCirurgiaPdtSalaVO.getDthrFimPdt();
			
			if (((dthrInicio.after(dthrInicioPdt) || dthrInicio
					.equals(dthrInicioPdt)) && dthrInicio.before(dthrFimPdt))
					|| (dthrInicio.before(dthrInicioPdt) && dthrFim.after(dthrFimPdt))) {
				
				AipPacientes paciente = getPacienteFacade().obterPaciente(
						descricaoCirurgiaPdtSalaVO.getPacCodigo());
				
				throw new ApplicationBusinessException(
						LiberacaoLaudoPreliminarONExceptionCode.ERRO_DESCRICAO_COLISAO_HORARIOS_PDT,
						paciente.getNome(), descricaoCirurgiaPdtSalaVO.getSala(),
						DateUtil.obterDataFormatada(dthrInicioPdt, DATE_PATTERN_DDMMYYYYHHMM),
						DateUtil.obterDataFormatada(dthrFimPdt, DATE_PATTERN_DDMMYYYYHHMM));
			}
		}
	}	
	
	private void formatarEquipe(List<DescricaoCirurgiaPdtSalaVO> listaDescricaoCirurgicaSala) {
		
		// Aplica nvl e substr
		for (DescricaoCirurgiaPdtSalaVO descricaoCirurgiaPdtSalaVO : listaDescricaoCirurgicaSala) {
			String equipe = descricaoCirurgiaPdtSalaVO.getNomeUsual();

			if (equipe == null) {
				equipe = descricaoCirurgiaPdtSalaVO.getNome().substring(0, 15);
			}

			descricaoCirurgiaPdtSalaVO.setEquipe(equipe);
		}
	}
	
	private void validarDthrInicioDthrFim(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {
		if(dthrFim == null || dthrInicio == null){
			throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00143);
		}
		if (dthrFim.before(dthrInicio) || dthrFim.equals(dthrInicio)) {
			// Data Final da cirurgia deve ser Maior que a Data Inicial.
			throw new ApplicationBusinessException(LiberacaoLaudoPreliminarONExceptionCode.PDT_00138);
		}
	}
	
	private void validarDthrInicioDataCirurgia(Date dthrInicio, Date dataCirurgia) throws ApplicationBusinessException {
		if (dthrInicio.before(dataCirurgia)) {
			// Data de início da cirurgia não pode ser menor que a data da cirurgia realizada.
			throw new ApplicationBusinessException(LiberacaoLaudoPreliminarONExceptionCode.PDT_00139);
		}
	}
	
	private void validarPrazoDthrInicioDataCirurgia(Date dthrInicio, Date dataCirurgia) throws ApplicationBusinessException {
		if (dthrInicio.after(DateUtil.adicionaDias(dataCirurgia, 2))) {
			// Data de início da cirurgia não pode ser maior que 48h da data da cirurgia realizada.
			throw new ApplicationBusinessException(LiberacaoLaudoPreliminarONExceptionCode.PDT_00140);
		}
	}
	
	public void validarTempoMinimoCirurgia(Date dthrInicio, Date dthrFim, Short tempoMinimo) throws ApplicationBusinessException {
		if (getBlocoCirurgicoFacade().validarTempoMinimoCirurgia(dthrInicio, dthrFim, tempoMinimo)) {
			// A duração desta cirurgia ultrapassa o dobro de tempo considerado mínimo para realização. Verifique!
			// throw new ApplicationBusinessException(LiberacaoLaudoPreliminarONExceptionCode.MBC_01096);
			generateMessage(Severity.WARN, "MBC_01096");
		}
	}
	
	public boolean ultrapassaTempoMinimoCirurgia(final Date dthrInicioProcedimento, final Date dthrFimProcedimento, final DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		List<PdtProc> listaProc = getIBlocoCirurgicoProcDiagTerapFacade().pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(descricaoProcDiagTerapVO.getDdtSeq());
		
		MbcProcedimentoCirurgicos proced = null;
		
		if (!listaProc.isEmpty()) {
			proced = listaProc.get(0).getPdtProcDiagTerap().getProcedimentoCirurgico();
		}
		
		if(proced != null) {
			return getBlocoCirurgicoFacade().validarTempoMinimoCirurgia(dthrInicioProcedimento, dthrFimProcedimento, proced.getTempoMinimo());
		}
		
		return false;
	}
	
	private void validarColisaoHorarioDescricaoCirurgicaDthrFim(Date dthrInicio, Date dthrFim, 
			List<DescricaoCirurgiaPdtSalaVO> listaDescricaoCirurgicaSala) throws ApplicationBusinessException {
		
		// Verifica colisão de horários com outra descrição cirurgica - dthr fim 
		for (DescricaoCirurgiaPdtSalaVO descricaoCirurgiaPdtSalaVO : listaDescricaoCirurgicaSala) {
			Date dthrInicioCrg = descricaoCirurgiaPdtSalaVO.getDthrInicioCirg();
			Date dthrFimCrg = descricaoCirurgiaPdtSalaVO.getDthrFimCirg();
			
			if (dthrFim.after(dthrInicioCrg) && (dthrFim.before(dthrFimCrg) || dthrFim.equals(dthrFimCrg))) {
				
				AipPacientes paciente = getPacienteFacade().obterPaciente(
						descricaoCirurgiaPdtSalaVO.getPacCodigo());

				throw new ApplicationBusinessException(
						LiberacaoLaudoPreliminarONExceptionCode.ERRO_DESCRICAO_COLISAO_HORARIOS_CIRURGIA,
						paciente.getNome(), descricaoCirurgiaPdtSalaVO.getSala(),
						DateUtil.obterDataFormatada(dthrInicioCrg, DATE_PATTERN_DDMMYYYYHHMM),
						DateUtil.obterDataFormatada(dthrFimCrg, DATE_PATTERN_DDMMYYYYHHMM));
			}
			
			if (dthrInicio.before(dthrInicioCrg) && (dthrFim.after(dthrFimCrg))) {
				
				AipPacientes paciente = getPacienteFacade().obterPaciente(
						descricaoCirurgiaPdtSalaVO.getPacCodigo());

				throw new ApplicationBusinessException(
						LiberacaoLaudoPreliminarONExceptionCode.ERRO_DESCRICAO_COLISAO_HORARIOS_CIRURGIA,
						paciente.getNome(), descricaoCirurgiaPdtSalaVO.getSala(),
						DateUtil.obterDataFormatada(dthrInicioCrg, DATE_PATTERN_DDMMYYYYHHMM),
						DateUtil.obterDataFormatada(dthrFimCrg, DATE_PATTERN_DDMMYYYYHHMM));
			}
		}
	}
	
	private void validarColisaoHorarioDescricaoPdtDthrFim(Date dthrInicio, Date dthrFim, 
			List<DescricaoCirurgiaPdtSalaVO> listaDescricaoPdtSala) throws ApplicationBusinessException {
		
		// Verifica colisão de horários com descrição de PDT
		for (DescricaoCirurgiaPdtSalaVO descricaoCirurgiaPdtSalaVO : listaDescricaoPdtSala) {
			Date dthrInicioPdt = descricaoCirurgiaPdtSalaVO.getDthrInicioPdt();
			Date dthrFimPdt = descricaoCirurgiaPdtSalaVO.getDthrFimPdt();
			
			if (dthrFim.after(dthrInicioPdt) && (dthrFim.before(dthrFimPdt) || dthrFim.equals(dthrFimPdt))) {
				
				AipPacientes paciente = getPacienteFacade().obterPaciente(
						descricaoCirurgiaPdtSalaVO.getPacCodigo());

				throw new ApplicationBusinessException(
						LiberacaoLaudoPreliminarONExceptionCode.ERRO_DESCRICAO_COLISAO_HORARIOS_PDT,
						paciente.getNome(), descricaoCirurgiaPdtSalaVO.getSala(),
						DateUtil.obterDataFormatada(dthrInicioPdt, DATE_PATTERN_DDMMYYYYHHMM),
						DateUtil.obterDataFormatada(dthrFimPdt, DATE_PATTERN_DDMMYYYYHHMM));
			}
			
			if (dthrInicio.before(dthrInicioPdt) && (dthrFim.after(dthrFimPdt))) {
				
				AipPacientes paciente = getPacienteFacade().obterPaciente(
						descricaoCirurgiaPdtSalaVO.getPacCodigo());

				throw new ApplicationBusinessException(
						LiberacaoLaudoPreliminarONExceptionCode.ERRO_DESCRICAO_COLISAO_HORARIOS_PDT,
						paciente.getNome(), descricaoCirurgiaPdtSalaVO.getSala(),
						DateUtil.obterDataFormatada(dthrInicioPdt, DATE_PATTERN_DDMMYYYYHHMM),
						DateUtil.obterDataFormatada(dthrFimPdt, DATE_PATTERN_DDMMYYYYHHMM));
			}
		}
	}
	
	private void validarResultadoNormalOuCid(PdtDescricao descricao) throws ApplicationBusinessException {
	    //****************
	    //***VALIDAÇÕES***
	    //****************
		List<PdtCidDesc> lista = this.getPdtCidDescDAO().pesquisarPdtCidDescPorDdtSeq(descricao.getSeq());
	    if ((lista == null || lista.isEmpty()) && Boolean.FALSE.equals(descricao.getResultadoNormal())) {
	            throw new ApplicationBusinessException(PdtCidDescONExceptionCode.PDT_00173);
	    }
	}
	
	private void verificarPreenchimentoDatasProcedimento(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {
		if (dthrInicio == null || dthrFim == null) {
			// Informe data/hora de inicio e fim do procedimento na pasta P Diag Terap!
			throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00143);
		}
	}
	
	public void validarDatasDadoDesc(final Date dthrInicioProcedimento, final Date dthrFimProcedimento, final DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) 
			throws ApplicationBusinessException {
		
		if (DateUtil.validaDataMaiorIgual(dthrFimProcedimento, new Date())) {
			throw new ApplicationBusinessException(DescricaoRNExceptionCode.MSG_FIM_CRG_MENOR_DATA_ATUAL);
		}

		verificarPreenchimentoDatasProcedimento(dthrInicioProcedimento, dthrFimProcedimento);

		Integer crgSeq = descricaoProcDiagTerapVO.getDdtCrgSeq();
		MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(crgSeq);
		MbcSalaCirurgica salaCirurgica = cirurgia.getSalaCirurgica();
		
		List<PdtProc> listaProc = getIBlocoCirurgicoProcDiagTerapFacade().pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(descricaoProcDiagTerapVO.getDdtSeq());
		
		MbcProcedimentoCirurgicos proced = null;
		
		if (!listaProc.isEmpty()) {
			proced = listaProc.get(0).getPdtProcDiagTerap().getProcedimentoCirurgico();
		}
		
		if (proced != null && salaCirurgica != null) {
			
			//validarTempoMinimoCirurgia(dthrInicioProcedimento, dthrFimProcedimento, proced.getTempoMinimo());
			
			verificarColisaoDatasProcedimento(
					descricaoProcDiagTerapVO.getUnidadeFuncional().getSeq(), 
					cirurgia.getData(),
					crgSeq, salaCirurgica.getId().getUnfSeq(),
					salaCirurgica.getId().getSeqp(),
					dthrInicioProcedimento, dthrFimProcedimento);
		}
		
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected PdtCidDescDAO getPdtCidDescDAO() {
		return pdtCidDescDAO;
	}
	
	protected PdtDadoDescDAO getPdtDadoDescDAO() {
		return pdtDadoDescDAO;
	}
	
	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
		return mbcSalaCirurgicaDAO;
	}
	
	protected PdtDescricaoRN getPdtDescricaoRN() {
		return pdtDescricaoRN; 
	}
	
	protected DescricaoProcDiagTerapRN getDescricaoProcDiagTerapRN() {
		return descricaoProcDiagTerapRN;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}
	
	protected IBlocoCirurgicoProcDiagTerapFacade getIBlocoCirurgicoProcDiagTerapFacade() {
		return iBlocoCirurgicoProcDiagTerapFacade;
	}

}
