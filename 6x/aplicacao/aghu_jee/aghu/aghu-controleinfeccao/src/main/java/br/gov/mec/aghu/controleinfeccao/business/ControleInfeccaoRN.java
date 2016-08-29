package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.dao.MciGrupoProcedRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciGrupoProcedRiscoJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciProcedimentoRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciProcedimentoRiscoJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTipoGrupoProcedRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.GrupoProcedRiscoVO;
import br.gov.mec.aghu.controleinfeccao.vo.ProcedRiscoVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.MciCriterioPortal;
import br.gov.mec.aghu.model.MciGrupoProcedRisco;
import br.gov.mec.aghu.model.MciGrupoProcedRiscoId;
import br.gov.mec.aghu.model.MciGrupoProcedRiscoJn;
import br.gov.mec.aghu.model.MciMapProcedPrescricao;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
import br.gov.mec.aghu.model.MciParamProcedRisco;
import br.gov.mec.aghu.model.MciProcedimentoRisco;
import br.gov.mec.aghu.model.MciProcedimentoRiscoJn;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ControleInfeccaoRN extends BaseBusiness {
	
	private static final long serialVersionUID = -1262088058722762242L;

	private static final Log LOG = LogFactory.getLog(ControleInfeccaoRN.class);

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MciProcedimentoRiscoDAO mciProcedRiscoDAO; 

	@Inject
	private MciGrupoProcedRiscoDAO mciGrupoProcedRiscoDAO;
	
	@Inject
	private MciGrupoProcedRiscoJnDAO mciGrupoProcedRiscoJnDAO;
	
	@Inject
	private MciTipoGrupoProcedRiscoDAO mciTipoGrupoProcedRiscoDAO;
	
	@Inject
	private MciProcedimentoRiscoJnDAO mciProcedimentoRiscoJnDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private enum ControleInfeccaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PERIODO_PROCEDIMENTO_RISCO , MSG_TOPO_PR_RESTRICAO_EXCLUSAO, MSG_TOPO_PR_RESTRICAO_EXCLUSAO_IMPRESSOES, MSG_TOPO_PR_RESTRICAO_EXCLUSAO_PRESCRICOES,
		MSG_TOPO_PR_RESTRICAO_EXCLUSAO_NOTIFICACOES, MSG_TOPO_PR_RESTRICAO_EXCLUSAO_PORTAL, ERRO_P_CCIH_NRO_DIAS_PERM_DEL, MENSAGEM_DADOS_INCOMPLETOS_PR, MENSAGEM_PERIODO_GRUPO;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Verifica se o leito é controlado pela CCIH.
	 * Retorna true se o leito for de interesse de controle pela CCIH.
	 * ORADB MCIK_LEITOS.MCIC_CONTROLADO_CCIH
	 * @param leitoID
	 * @return
	 */
	public boolean verificaLeitoControladoCCIH(String leitoID) {
			
		Boolean indAcompanhamentoCCIH = null;
		DominioSimNao indExclusivoInfeccao = null;

		AinLeitos leito =  internacaoFacade.obterLeitoPorId(leitoID);


		if (leitoID != null) {
			indAcompanhamentoCCIH = leito.getIndAcompanhamentoCcih();
			indExclusivoInfeccao = leito.getQuarto().getIndExclusivInfeccao();
		}

		return Boolean.TRUE.equals(indAcompanhamentoCCIH) || DominioSimNao.S.equals(indExclusivoInfeccao);
	}

	//#37968   RN01
	public void validarRemoverProcedimentoRisco(Short seq, Date criadoEm) throws ApplicationBusinessException, BaseListException{
		//RN 07
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		boolean rn4 = validaDiasProcedimentoRisco(parametro.getVlrNumerico().intValue(),criadoEm);
		
		boolean rn1 = validarRegistroAssociado(seq);
		
		if(rn1 && rn4){
			List<MciGrupoProcedRisco> lista = mciGrupoProcedRiscoDAO.pesquisarMciGrupoProcedRiscoPor(seq);
			for (MciGrupoProcedRisco item : lista) {
				persistirGrupoProcedRiscoJournal(item,DominioOperacoesJournal.DEL);
				mciGrupoProcedRiscoDAO.remover(item);
			}
			MciProcedimentoRisco entity = mciProcedRiscoDAO.obterProcedimento(seq);
			persistirProcedRiscoJournal(entity,DominioOperacoesJournal.DEL);
			mciProcedRiscoDAO.remover(entity);
		}
	}
	
	public void atualizaProcedimentoRisco(MciProcedimentoRisco entity, DominioSituacao situacao)throws ApplicationBusinessException{
		persistirProcedRiscoJournal(entity,DominioOperacoesJournal.UPD);
		entity.setIndSituacao(situacao);
		entity.setIndInformacaoHora("N");
		mciProcedRiscoDAO.merge(entity);
	}	
	
	
	// #37968 
	public void persistirProcedRiscoJournal(MciProcedimentoRisco obj, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MciProcedimentoRiscoJn journal = BaseJournalFactory.getBaseJournal(operacao, MciProcedimentoRiscoJn.class, servidorLogado.getUsuario());
		journal.setSeq(obj.getSeq());
		journal.setDescricao(obj.getDescricao());
		journal.setIndSituacao(obj.getIndSituacao());
		journal.setGrauRisco(obj.getGrauRisco());
		journal.setIndPermSobreposicao(obj.getIndPermSobreposicao());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setAlteradoEm(obj.getAlteradoEm());
		
		ProcedRiscoVO vo = mciProcedRiscoDAO.obterProcedRiscoComRelacionamento(obj.getSeq());
		journal.setSerVinCodigo(vo.getVinCodigo());
		journal.setSerMatricula(vo.getMatricula());

		if(vo.getVinCodigoMovi() != null){
			journal.setSerVinCodigoMovimentado(vo.getVinCodigoMovi());
			journal.setSerMatriculaMovimentado(vo.getMatriculaMovi());
		}
		
		mciProcedimentoRiscoJnDAO.persistir(journal);		
	}
	
	public boolean validaDiasProcedimentoRisco(Integer parametroDias,Date criadoEm) throws ApplicationBusinessException{
		Date data = new Date(System.currentTimeMillis()); 
		Integer qtd = DateUtil.calcularDiasEntreDatas(criadoEm,data);
		if(qtd > parametroDias){			
			throw new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MENSAGEM_PERIODO_PROCEDIMENTO_RISCO);
		}		
		return true;
	}
	
	public boolean validarRegistroAssociado(Short seq) throws BaseListException{
		List<MciParamProcedRisco> impressoes = mciProcedRiscoDAO.pesquisarImpressao(seq);
		List<MciMvtoProcedimentoRiscos> notificacoes = mciProcedRiscoDAO.pesquisarNotificacao(seq);
		List<MciMapProcedPrescricao> prescricoes = mciProcedRiscoDAO.pesquisarPrescricao(seq);
		List<MciCriterioPortal> criterios = mciProcedRiscoDAO.pesquisarCriterio(seq);
		
		boolean habilitarException = false;
		BaseListException listaDeErros = new BaseListException();
		listaDeErros.add(new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MSG_TOPO_PR_RESTRICAO_EXCLUSAO));
		
		if(impressoes.size() > 0 || notificacoes.size() > 0 || prescricoes.size() > 0 || criterios.size() > 0){
			if(impressoes.size() > 0){
				habilitarException =  true;
				for (MciParamProcedRisco item : impressoes) {
					listaDeErros.add(new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MSG_TOPO_PR_RESTRICAO_EXCLUSAO_IMPRESSOES, item.getId().getPruSeq()));
				}	
			}
			if(prescricoes.size() > 0){
				habilitarException =  true;
				for (MciMapProcedPrescricao item : prescricoes) {
					listaDeErros.add(new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MSG_TOPO_PR_RESTRICAO_EXCLUSAO_PRESCRICOES, item.getSeq()));
				}
			}
			if(notificacoes.size() > 0){
				habilitarException =  true;
				for (MciMvtoProcedimentoRiscos item : notificacoes) {
					listaDeErros.add(new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MSG_TOPO_PR_RESTRICAO_EXCLUSAO_NOTIFICACOES, item.getSeq()));
				}
			}
			if(criterios.size() > 0){
				habilitarException =  true;
				for (MciCriterioPortal item : criterios) {
					listaDeErros.add(new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MSG_TOPO_PR_RESTRICAO_EXCLUSAO_PORTAL, item.getSeq()));
				}
			}
			if (habilitarException && listaDeErros.hasException()) {
				throw listaDeErros;
			}
		}
		return true;
	}
	
	// #37968 RN03
	public void validaeInsereProcedimentoRisco(MciProcedimentoRisco entity) throws ApplicationBusinessException{
		if(entity.getDescricao() == null){
			throw new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MENSAGEM_DADOS_INCOMPLETOS_PR);	
		}
		entity.setRapServidoresByMciPorSerFk1(servidorLogadoFacade.obterServidorLogado());
		entity.setCriadoEm(new Date());
		entity.setGrauRisco((short)0);
		entity.setIndPermSobreposicao("S");
		mciProcedRiscoDAO.inserir(entity);
	}
	
	public void validarNumeroDiasDecorridosCriacaoRegistro(Date dataCriacao, BusinessExceptionCode exceptionCode)throws BaseException {
		if(obterDiferencaEntreDatas(dataCriacao) > obterParametroNumeroDiasDecorridosCriacaoRegistro()){
			throw new ApplicationBusinessException(exceptionCode);
		}
	}
	
	private Integer obterDiferencaEntreDatas(Date dataCriacao){
		return DateUtil.calcularDiasEntreDatas(dataCriacao, new Date(System.currentTimeMillis()));
	}
	
	public Long obterParametroNumeroDiasDecorridosCriacaoRegistro() throws BaseException {
		return (Long) obterParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);	
	}
	
	private Long obterParametro(AghuParametrosEnum parametrosEnum) throws BaseException {		
		try {
			return parametroFacade.buscarValorLong(parametrosEnum);
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.ERRO_P_CCIH_NRO_DIAS_PERM_DEL);
		}		
	}
	
	public void notNull(Object object, BusinessExceptionCode exceptionCode) throws ApplicationBusinessException { 
		try {
			Validate.notNull(object);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(exceptionCode);
		}
	}
	
	// #37968 RN03
	public void associarGrupoProced(Short porSeq,Short tgpSeq) throws ApplicationBusinessException{
		MciProcedimentoRisco procedimento = mciProcedRiscoDAO.obterPorChavePrimaria(porSeq);
		MciTipoGrupoProcedRisco tipoGrupo = mciTipoGrupoProcedRiscoDAO.obterPorChavePrimaria(tgpSeq);
		
		MciGrupoProcedRiscoId id = new MciGrupoProcedRiscoId();
		id.setPorSeq(porSeq);
		id.setTgpSeq(tgpSeq);
		
		MciGrupoProcedRisco entity = new MciGrupoProcedRisco();
		entity.setRapServidoresByMciGrsSerFk2(servidorLogadoFacade.obterServidorLogado());
		entity.setCriadoEm(new Date());
		entity.setIndSituacao(DominioSituacao.A);
		entity.setMciProcedimentoRisco(procedimento);
		entity.setMciTipoGrupoProcedRisco(tipoGrupo);
		entity.setId(id);
		mciGrupoProcedRiscoDAO.persistir(entity);
	}
	
	public boolean validaDiasGrupo(Integer parametroDias,Date criadoEm) throws ApplicationBusinessException{
		Date data = new Date(System.currentTimeMillis()); 
		Integer qtd = DateUtil.calcularDiasEntreDatas(criadoEm,data);
		if(qtd > parametroDias){
			throw new ApplicationBusinessException(ControleInfeccaoRNExceptionCode.MENSAGEM_PERIODO_GRUPO);
		}		
		return true;
	}
	
	// #37968 
	public void persistirGrupoProcedRiscoJournal(MciGrupoProcedRisco obj, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MciGrupoProcedRiscoJn journal = BaseJournalFactory.getBaseJournal(operacao, MciGrupoProcedRiscoJn.class, servidorLogado.getUsuario());
		journal.setPorSeq(obj.getId().getPorSeq());
		journal.setTgpSeq(obj.getId().getTgpSeq());
		journal.setIndSituacao(obj.getIndSituacao());
		journal.setCriadoEm(obj.getCriadoEm());
		
		GrupoProcedRiscoVO vo = mciGrupoProcedRiscoDAO.obterGrupoProcedRiscoComRelacionamento(obj.getId());
		journal.setSerVinCodigo(vo.getVinCodigo());
		journal.setSerMatricula(vo.getMatricula());

		mciGrupoProcedRiscoJnDAO.persistir(journal);		
	}
	
	
	// #37968 
	public void excluirProcedimentoRisco(Short tgpSeq,Short porSeq) throws ApplicationBusinessException{		
		MciGrupoProcedRiscoId id = new MciGrupoProcedRiscoId();
		id.setPorSeq(porSeq);
		id.setTgpSeq(tgpSeq);
		MciGrupoProcedRisco entity = mciGrupoProcedRiscoDAO.obterPorChavePrimaria(id);
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		validaDiasGrupo(parametro.getVlrNumerico().intValue(),entity.getCriadoEm());
		
		persistirGrupoProcedRiscoJournal(entity,DominioOperacoesJournal.DEL);
		
		mciGrupoProcedRiscoDAO.remover(entity);
	}
}

