package br.gov.mec.aghu.transplante.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.transplante.dao.MtxDoencaBasesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MtxDoencasBaseON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MtxDoencasBaseON.class);
	
		
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MtxDoencaBasesDAO mtxDoencaBasesDAO;
	
//	@EJB
//	private IAghuFacade aghuFacade;
//	
	@EJB
	private IParametroFacade parametroFacade;
	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	private static final long serialVersionUID = -3059459228465751522L;

	public enum OrigemInfeccaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PERIODO_EXCLUSAO;
	}
	
	public boolean excluirDoencaBase(MtxDoencaBases mtxDoencaBase) {
		
		if (!this.mtxDoencaBasesDAO.existeRegistroTransplante(mtxDoencaBase)){
			MtxDoencaBases entidade = this.mtxDoencaBasesDAO.obterPorChavePrimaria(mtxDoencaBase.getSeq());
			this.mtxDoencaBasesDAO.remover(entidade);
			return true;
		}
		return false;
	}
	
	public boolean inserirDoencaBase(MtxDoencaBases mtxDoencaBase){
		if(mtxDoencaBase != null){
			if (!this.mtxDoencaBasesDAO.pesquisarRegistrosIguais(mtxDoencaBase)){
				this.mtxDoencaBasesDAO.persistir(mtxDoencaBase);
				return true;		
			}		
		}
		return false;
	}
	
	public boolean atualizarDoencaBase(MtxDoencaBases mtxDoencaBase) {
		if(mtxDoencaBase != null){
			if (!this.mtxDoencaBasesDAO.atualisarRegistrosIguais(mtxDoencaBase)){
				this.mtxDoencaBasesDAO.atualizar(mtxDoencaBase);
				return true;	
			}
		}
		return false;
	}
//		
//		MciEtiologiaInfeccao etiologiaInfeccaoOriginal = this.mciEtiologiaInfeccaoDAO.obterOriginal(origemInfeccao.getCodigoOrigem());
//		
//		MciEtiologiaInfeccao etiologiaInfeccao = this.mciEtiologiaInfeccaoDAO.obterPorChavePrimaria(origemInfeccao.getCodigoOrigem());
//		etiologiaInfeccao.setSituacao(origemInfeccao.getSituacao());
//		etiologiaInfeccao.setTextoNotificacao(origemInfeccao.getTextoNotificacao());
//		if (origemInfeccao.getUnfSeq() != null) {
//			etiologiaInfeccao.setUnidadeFuncional(this.aghuFacade.obterUnidadeFuncional(origemInfeccao.getUnfSeq()));
//			
//		} else {
//			etiologiaInfeccao.setUnidadeFuncional(null);
//		}
//		this.mciEtiologiaInfeccaoON.atualizarMciEtiologiaInfeccao(etiologiaInfeccao, etiologiaInfeccaoOriginal);
//	}
	
	
//	public void salvarResultadoExames(MtxResultadoExames resultadoExames, Integer seqTransplante) {
//		
//		if (resultadoExames != null) {
//			
//		MtxTransplantes transplante =mtxTransplantesDAO.obterPorChavePrimaria(seqTransplante);
//		resultadoExames.setTransplante(transplante);
//			mtxResultadoExamesDAO.persistir(resultadoExames);
//			}
//	}
	
	
//	public List<MtxDoencaBases> obterListaDoencasBase(Integer firstResult,
//			Integer maxResults, String orderProperty, boolean asc,
//			String codigoOrigem, String descricao, DominioSituacao situacao) {
//		
//		List<OrigemInfeccoesVO> listaRetorno = this.mciEtiologiaInfeccaoDAO.listarOrigemInfeccoes(firstResult, maxResults, orderProperty, asc, codigoOrigem, descricao, situacao);
//		
//		return listaRetorno;
//	}
	
//	public void atualizarOrigemInfeccao(OrigemInfeccoesVO origemInfeccao) {
//		
//		MciEtiologiaInfeccao etiologiaInfeccaoOriginal = this.mciEtiologiaInfeccaoDAO.obterOriginal(origemInfeccao.getCodigoOrigem());
//		
//		MciEtiologiaInfeccao etiologiaInfeccao = this.mciEtiologiaInfeccaoDAO.obterPorChavePrimaria(origemInfeccao.getCodigoOrigem());
//		etiologiaInfeccao.setSituacao(origemInfeccao.getSituacao());
//		etiologiaInfeccao.setTextoNotificacao(origemInfeccao.getTextoNotificacao());
//		if (origemInfeccao.getUnfSeq() != null) {
//			etiologiaInfeccao.setUnidadeFuncional(this.aghuFacade.obterUnidadeFuncional(origemInfeccao.getUnfSeq()));
//			
//		} else {
//			etiologiaInfeccao.setUnidadeFuncional(null);
//		}
//		this.mciEtiologiaInfeccaoON.atualizarMciEtiologiaInfeccao(etiologiaInfeccao, etiologiaInfeccaoOriginal);
//	}
//	
//	public void inserirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) {
//		
//		MciLocalEtiologia localEtiologia = new MciLocalEtiologia();
//		
//		MciLocalEtiologiaId id = new MciLocalEtiologiaId(localOrigemInfeccao.getCodigoOrigem(), localOrigemInfeccao.getUnfSeq());
//		localEtiologia.setId(id);
//		localEtiologia.setIndFormaContabilizacao(localOrigemInfeccao.getIndFormaContabilizacao());
//		localEtiologia.setIndSituacao(localOrigemInfeccao.getIndSituacao());
//		
//		this.mciLocalEtiologiaON.inserirMciLocalEtiologia(localEtiologia);
//	}
//	
//	public void alterarLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) {
//		
//		MciLocalEtiologiaId id = new MciLocalEtiologiaId(localOrigemInfeccao.getCodigoOrigem(), localOrigemInfeccao.getUnfSeq());
//		MciLocalEtiologia localEtiologiaOriginal = this.mciLocalEtiologiaDAO.obterOriginal(id);
//		
//		MciLocalEtiologia localEtiologia = this.mciLocalEtiologiaDAO.obterPorChavePrimaria(id);
//		localEtiologia.setIndFormaContabilizacao(localOrigemInfeccao.getIndFormaContabilizacao());
//		localEtiologia.setIndSituacao(localOrigemInfeccao.getIndSituacao());
//		
//		this.mciLocalEtiologiaON.alterarMciLocalEtiologia(localEtiologia, localEtiologiaOriginal);
//	}
//	
//	public String excluirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) throws ApplicationBusinessException {
//		AghParametros parametro = this.getParametroFacade().buscarAghParametro(
//				AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
//		
//		String msgErroExclusao = null;
//		
//		MciLocalEtiologiaId id = new MciLocalEtiologiaId(localOrigemInfeccao.getCodigoOrigem(), localOrigemInfeccao.getUnfSeq());
//		
//		MciLocalEtiologia localEtiologia = this.mciLocalEtiologiaDAO.obterPorChavePrimaria(id);
//		
//		Integer numeroDias = DateUtil.obterQtdDiasEntreDuasDatas(localEtiologia.getCriadoEm(), new Date());
//		
//		if (CoreUtil.maior(numeroDias, parametro.getVlrNumerico())) {
//			throw new ApplicationBusinessException(OrigemInfeccaoONExceptionCode.MENSAGEM_PERIODO_EXCLUSAO);
//		}
//		List<Integer> listaTopografias = this.mciMvtoInfeccaoTopografiasDAO
//				.listarMvtosInfeccoesTopografiasPorUnfSeqTipoOrigem(localOrigemInfeccao.getUnfSeq(), localOrigemInfeccao.getCodigoOrigem());
//		
//		List<Integer> listaMedidas = this.mciMvtoMedidaPreventivasDAO.
//				listarMvtosMedidasPreventivasPorUnfSeqTipoOrigem(localOrigemInfeccao.getUnfSeq(), localOrigemInfeccao.getCodigoOrigem());
//		
//		if (!listaTopografias.isEmpty() || !listaMedidas.isEmpty()) {
//			msgErroExclusao = "Local de Origem não pode ser excluído porque está associado:";
//			
//			if (!listaTopografias.isEmpty()) {
//				msgErroExclusao = msgErroExclusao.concat("\n à(s) prescrições: ");
//				for (Integer seq : listaTopografias) {
//					msgErroExclusao = msgErroExclusao.concat("\n").concat(seq.toString());
//				}
//			}
//			if (!listaMedidas.isEmpty()) {
//				msgErroExclusao = msgErroExclusao.concat("\n à(s) notificações: ");
//				for (Integer seq : listaMedidas) {
//					msgErroExclusao = msgErroExclusao.concat("\n").concat(seq.toString());
//				}
//			}
//			return msgErroExclusao;
//		}
//		this.mciLocalEtiologiaON.excluirMciLocalEtiologia(localEtiologia);
//		return null;
//	}
}
