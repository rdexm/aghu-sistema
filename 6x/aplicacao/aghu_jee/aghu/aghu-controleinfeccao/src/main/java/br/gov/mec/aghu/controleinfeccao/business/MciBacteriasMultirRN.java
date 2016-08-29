package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.MciBacteriasAssociadasVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciBacteriaMultirDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciBacteriaMultirJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciCriterioGmrDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.model.MciBacteriaMultirJn;
import br.gov.mec.aghu.model.MciCriterioGmr;
import br.gov.mec.aghu.model.MciNotificacaoGmr;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciBacteriasMultirRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(MciBacteriasMultirRN.class);

@Override
@Deprecated
protected Log getLogger() {
	return LOG;
}

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private MciBacteriaMultirDAO mciBacteriaMultirDAO;

@Inject
private MciBacteriaMultirJnDAO mciBacteriaMultirJnDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private MciCriterioGmrDAO mciCriterioGmrDAO;

@Inject 
private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1262088053722762242L;
	
	private enum ManterBacteriasMultirExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DADOS_INCOMPLETOS_BACTERIAS_MULTIR, MENSAGEM_PERIODO_BACTERIAS_MULTIR,
		MENSAGEM_REST_EXC_BACTERIAS_MULTIR, MENSAGEM_REST_EXC_CRITERIOS_BACTERIAS_MULTIR, 
		MENSAGEM_REST_EXC_NOTIFICACOES_BACTERIAS_MULTIR, MENSAGEM_REST_EXC_BACTERIAS_BACTERIAS_MULTIR,
		MENSAGEM_REGISTRO_DUPLICADO_BACTERIAS_MULTIR;
	}

	public void validarInsereBacteriaMultir(MciBacteriaMultir entity) throws ApplicationBusinessException{
		validarCampoDescricaoNuloOuJaExistente(entity);
		entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		this.mciBacteriaMultirDAO.persistir(entity);
	}
	
	public void validarInsereBacteriaMultir(MciBacteriasAssociadasVO vo) throws ApplicationBusinessException{
		MciBacteriaMultir entity = new MciBacteriaMultir();
		entity.setMciBacteriaMultir(vo.getMciBacteriaMultir());
		entity.setDescricao(vo.getDescricao());
		entity.setCriadoEm(vo.getCriadoEm());
		
		validarCampoDescricaoNuloOuJaExistente(entity);
		
		entity.setSituacao(vo.getIndSituacao());
		entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		this.mciBacteriaMultirDAO.persistir(entity);
	}

	private void validarCampoDescricaoNuloOuJaExistente(MciBacteriaMultir objBacteriaAssociada)
			throws ApplicationBusinessException {
		if(objBacteriaAssociada.getDescricao() == null){
			throw new ApplicationBusinessException(ManterBacteriasMultirExceptionCode.MENSAGEM_DADOS_INCOMPLETOS_BACTERIAS_MULTIR);	
		}

		if(this.mciBacteriaMultirDAO.validarDescricaoJaExistente(objBacteriaAssociada.getDescricao())) {
			throw new ApplicationBusinessException(ManterBacteriasMultirExceptionCode.MENSAGEM_REGISTRO_DUPLICADO_BACTERIAS_MULTIR);	
		}
	}	
	
	public void atualizarBacteriaMultir(MciBacteriaMultir entity) throws ApplicationBusinessException{
		entity.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());
		MciBacteriaMultir entityJournal = this.mciBacteriaMultirDAO.obterPorChavePrimaria(entity.getSeq());
		persistirBacteriasMultirJournal(entityJournal, DominioOperacoesJournal.UPD);
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		entity.setAlteradoEm(new Date());
		entity.setServidorMovimentado(servidorLogado);
		
		this.mciBacteriaMultirDAO.merge(entity);
	}	
	
	public void alterarBacteriaAssociada(MciBacteriasAssociadasVO bacteriaAssociada) throws ApplicationBusinessException {
		
		MciBacteriaMultir entity = this.mciBacteriaMultirDAO.obterPorChavePrimaria(bacteriaAssociada.getSeq());
		
		//entity.setMciBacteriaMultir(bacteriaAssociada.getMciBacteriaMultir());
		entity.setSituacao(bacteriaAssociada.getIndSituacao());
		entity.setAlteradoEm(new Date());
		entity.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());

		// Journal
		MciBacteriaMultir entityJournal = this.mciBacteriaMultirDAO.obterPorChavePrimaria(entity.getSeq());
		persistirBacteriasMultirJournal(entityJournal, DominioOperacoesJournal.UPD);
		
		this.mciBacteriaMultirDAO.merge(entity);
	}
	
	// #37923 RN1
	public void excluirMciBacteriaMultir(Integer seq) throws ApplicationBusinessException, BaseListException {
		MciBacteriaMultir mciBacteriaMultir = this.mciBacteriaMultirDAO.obterPorChavePrimaria(seq);
		
		this.validarDataCriacaoMciBacteriaMultir(mciBacteriaMultir);
		this.verificarRestricaoExclusao(seq);		
		this.persistirBacteriasMultirJournal(mciBacteriaMultir, DominioOperacoesJournal.DEL);
		
		mciBacteriaMultirDAO.remover(mciBacteriaMultir);
	}
	
	// #37923 RN6
	private void validarDataCriacaoMciBacteriaMultir(MciBacteriaMultir mciBacteriaMultir) throws ApplicationBusinessException {
		
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		Float qtDias = CoreUtil.diferencaEntreDatasEmDias(new Date(), mciBacteriaMultir.getCriadoEm());
		
		if (qtDias > parametro.getVlrNumerico().floatValue()) {
			throw new ApplicationBusinessException(ManterBacteriasMultirExceptionCode.MENSAGEM_PERIODO_BACTERIAS_MULTIR);
		}
		
	}
	
	private void verificarRestricaoExclusao(Integer bmrSeq) throws BaseListException {
		List<MciCriterioGmr> criterios = this.mciCriterioGmrDAO.pesquisarMciCriterioGrmPorBmrSeq(bmrSeq);
		List<MciNotificacaoGmr> notificacoes = this.mciNotificacaoGmrDAO.pesquisarNotificacaoGrmPorBmrSeq(bmrSeq);
		List<MciBacteriaMultir> bacteriasAssociadas = this.mciBacteriaMultirDAO.pesquisarBacteriaMultirAssociadaPorBrmSeq(bmrSeq);

		boolean habilitarException = false;
		BaseListException listaDeErros = new BaseListException();
		listaDeErros.add(new ApplicationBusinessException(ManterBacteriasMultirExceptionCode.MENSAGEM_REST_EXC_BACTERIAS_MULTIR));
		int count;
		
		if (!criterios.isEmpty() || !notificacoes.isEmpty() || !bacteriasAssociadas.isEmpty()) {
			if(!criterios.isEmpty()){
				habilitarException =  true;
				count = 1;
				StringBuilder paramsCriterios = new StringBuilder();
				for (MciCriterioGmr item : criterios) {
					paramsCriterios.append(item.getId().getAmbSeq());
					if (count < criterios.size()){
						paramsCriterios.append(", ");
					}
					count++;
				}
				listaDeErros.add(new ApplicationBusinessException(ManterBacteriasMultirExceptionCode.MENSAGEM_REST_EXC_CRITERIOS_BACTERIAS_MULTIR, paramsCriterios));
			}
			if(!notificacoes.isEmpty()){
				habilitarException =  true;
				count = 1;
				StringBuilder paramsNotificacoes = new StringBuilder();
				for (MciNotificacaoGmr item : notificacoes) {
					paramsNotificacoes.append(item.getSeq());
					if (count < notificacoes.size()){
						paramsNotificacoes.append(", ");
					}
					count++;
				}
				listaDeErros.add(new ApplicationBusinessException(ManterBacteriasMultirExceptionCode.MENSAGEM_REST_EXC_NOTIFICACOES_BACTERIAS_MULTIR, paramsNotificacoes));				
			}
			if(!bacteriasAssociadas.isEmpty()){
				habilitarException =  true;
				count = 1;
				StringBuilder paramsBacterias = new StringBuilder();
				for (MciBacteriaMultir item : bacteriasAssociadas) {
					paramsBacterias.append(item.getSeq());
					if (count < bacteriasAssociadas.size()){
						paramsBacterias.append(", ");
					}
					count++;
				}
				listaDeErros.add(new ApplicationBusinessException(ManterBacteriasMultirExceptionCode.MENSAGEM_REST_EXC_BACTERIAS_BACTERIAS_MULTIR, paramsBacterias));				
			}

			if (habilitarException && listaDeErros.hasException()) {
				throw listaDeErros;
			}
		}	
	}
	
	private void persistirBacteriasMultirJournal(MciBacteriaMultir obj, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MciBacteriaMultirJn journal = BaseJournalFactory.getBaseJournal(operacao, MciBacteriaMultirJn.class, servidorLogado.getUsuario());
		journal.setSeqInt(obj.getSeq().intValue());
		journal.setDescricao(obj.getDescricao());
		journal.setIndSituacao(obj.getSituacao());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setAlteradoEm(obj.getAlteradoEm());
		journal.setSerVinCodigo(obj.getRapServidores().getId().getVinCodigo());
		journal.setSerMatricula(obj.getRapServidores().getId().getMatricula());

		if(obj.getServidorMovimentado() != null){
			journal.setSerVinCodigoMovimentado(obj.getServidorMovimentado().getId().getVinCodigo());
			journal.setSerMatriculaMovimentado(obj.getServidorMovimentado().getId().getMatricula());
		}
		this.mciBacteriaMultirJnDAO.persistir(journal);	
	}
	
	protected MciBacteriaMultirDAO getMciBacteriaMultirDAO() {
		return mciBacteriaMultirDAO;
	}
	
	protected MciBacteriaMultirJnDAO getMciBacteriaMultirJnDAO() {
		return mciBacteriaMultirJnDAO;
	}

	public List<MciBacteriasAssociadasVO> listarBacteriasAssociadas(Integer seq) {
		List<MciBacteriasAssociadasVO> listaRetorno = this.mciBacteriaMultirDAO.listarBacteriasAssociadas(seq);
		
		for (MciBacteriasAssociadasVO vo : listaRetorno) {
			vo.setSituacao(vo.getIndSituacao().isAtivo());
		}
		return listaRetorno;
	}
	
}

