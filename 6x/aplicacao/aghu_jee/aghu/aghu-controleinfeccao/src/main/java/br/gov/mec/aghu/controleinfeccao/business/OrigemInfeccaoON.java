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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciLocalEtiologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.vo.LocaisOrigemInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.MciLocalEtiologia;
import br.gov.mec.aghu.model.MciLocalEtiologiaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class OrigemInfeccaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(OrigemInfeccaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MciEtiologiaInfeccaoDAO mciEtiologiaInfeccaoDAO;
	
	@Inject
	private MciLocalEtiologiaDAO mciLocalEtiologiaDAO;
	
	@Inject
	private MciMvtoInfeccaoTopografiasDAO mciMvtoInfeccaoTopografiasDAO;
	
	@Inject
	private MciMvtoMedidaPreventivasDAO mciMvtoMedidaPreventivasDAO;
	
	@EJB
	private MciEtiologiaInfeccaoON mciEtiologiaInfeccaoON;
	
	@EJB
	private MciLocalEtiologiaON mciLocalEtiologiaON;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	private static final long serialVersionUID = -3059459228465751522L;

	public enum OrigemInfeccaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PERIODO_EXCLUSAO;
	}
	
	public List<OrigemInfeccoesVO> listarOrigemInfeccoes(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String codigoOrigem, String descricao, DominioSituacao situacao) {
		List<OrigemInfeccoesVO> listaRetorno = this.mciEtiologiaInfeccaoDAO.listarOrigemInfeccoes(firstResult, maxResults, orderProperty, asc, codigoOrigem, descricao, situacao);
		
		for (OrigemInfeccoesVO vo : listaRetorno) {
			if (vo.getUnfSeq() != null) {
				AghUnidadesFuncionais unidade = this.aghuFacade.obterUnidadeFuncional(vo.getUnfSeq());
				if (unidade != null) {
					vo.setDescricaoUnidadeFuncional(unidade.getLPADAndarAlaDescricao());
				}
			}
		}
		return listaRetorno;
	}
	
	public Long listarOrigemInfeccoesCount(String codigoOrigem, String descricao, DominioSituacao situacao) {
		return this.mciEtiologiaInfeccaoDAO.listarOrigemInfeccoesCount(codigoOrigem, descricao, situacao);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesAtivas(String strPesquisa, boolean semEtiologia, String tipo) {
		return this.aghuFacade.pesquisarUnidadesAtivas(strPesquisa, semEtiologia, tipo);
	}
	
	public List<LocaisOrigemInfeccaoVO> listarLocaisOrigemInfeccoes(String codigoOrigem) {
		List<LocaisOrigemInfeccaoVO> listaRetorno = this.mciLocalEtiologiaDAO.listarLocaisOrigemInfeccoes(codigoOrigem);
		
		for (LocaisOrigemInfeccaoVO vo : listaRetorno) {
			vo.setSituacao(vo.getIndSituacao().isAtivo());
			AghUnidadesFuncionais unidade = this.aghuFacade.obterUnidadeFuncional(vo.getUnfSeq());
			if (unidade != null) {
				vo.setDescricaoUnidadeFuncional(unidade.getLPADAndarAlaDescricao());
				vo.setDescricaoLocal(unidade.getDescricao());
			}
		}
		return listaRetorno;
	}
	
	public void atualizarOrigemInfeccao(OrigemInfeccoesVO origemInfeccao) {
		
		MciEtiologiaInfeccao etiologiaInfeccaoOriginal = this.mciEtiologiaInfeccaoDAO.obterOriginal(origemInfeccao.getCodigoOrigem());
		
		MciEtiologiaInfeccao etiologiaInfeccao = this.mciEtiologiaInfeccaoDAO.obterPorChavePrimaria(origemInfeccao.getCodigoOrigem());
		etiologiaInfeccao.setSituacao(origemInfeccao.getSituacao());
		etiologiaInfeccao.setTextoNotificacao(origemInfeccao.getTextoNotificacao());
		if (origemInfeccao.getUnfSeq() != null) {
			etiologiaInfeccao.setUnidadeFuncional(this.aghuFacade.obterUnidadeFuncional(origemInfeccao.getUnfSeq()));
			
		} else {
			etiologiaInfeccao.setUnidadeFuncional(null);
		}
		this.mciEtiologiaInfeccaoON.atualizarMciEtiologiaInfeccao(etiologiaInfeccao, etiologiaInfeccaoOriginal);
	}
	
	public void inserirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) {
		
		MciLocalEtiologia localEtiologia = new MciLocalEtiologia();
		
		MciLocalEtiologiaId id = new MciLocalEtiologiaId(localOrigemInfeccao.getCodigoOrigem(), localOrigemInfeccao.getUnfSeq());
		localEtiologia.setId(id);
		localEtiologia.setIndFormaContabilizacao(localOrigemInfeccao.getIndFormaContabilizacao());
		localEtiologia.setIndSituacao(localOrigemInfeccao.getIndSituacao());
		
		this.mciLocalEtiologiaON.inserirMciLocalEtiologia(localEtiologia);
	}
	
	public void alterarLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) {
		
		MciLocalEtiologiaId id = new MciLocalEtiologiaId(localOrigemInfeccao.getCodigoOrigem(), localOrigemInfeccao.getUnfSeq());
		MciLocalEtiologia localEtiologiaOriginal = this.mciLocalEtiologiaDAO.obterOriginal(id);
		
		MciLocalEtiologia localEtiologia = this.mciLocalEtiologiaDAO.obterPorChavePrimaria(id);
		localEtiologia.setIndFormaContabilizacao(localOrigemInfeccao.getIndFormaContabilizacao());
		localEtiologia.setIndSituacao(localOrigemInfeccao.getIndSituacao());
		
		this.mciLocalEtiologiaON.alterarMciLocalEtiologia(localEtiologia, localEtiologiaOriginal);
	}
	
	public String excluirLocalOrigemInfeccao(LocaisOrigemInfeccaoVO localOrigemInfeccao) throws ApplicationBusinessException {
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		
		String msgErroExclusao = null;
		
		MciLocalEtiologiaId id = new MciLocalEtiologiaId(localOrigemInfeccao.getCodigoOrigem(), localOrigemInfeccao.getUnfSeq());
		
		MciLocalEtiologia localEtiologia = this.mciLocalEtiologiaDAO.obterPorChavePrimaria(id);
		
		Integer numeroDias = DateUtil.obterQtdDiasEntreDuasDatas(localEtiologia.getCriadoEm(), new Date());
		
		if (CoreUtil.maior(numeroDias, parametro.getVlrNumerico())) {
			throw new ApplicationBusinessException(OrigemInfeccaoONExceptionCode.MENSAGEM_PERIODO_EXCLUSAO);
		}
		List<Integer> listaTopografias = this.mciMvtoInfeccaoTopografiasDAO
				.listarMvtosInfeccoesTopografiasPorUnfSeqTipoOrigem(localOrigemInfeccao.getUnfSeq(), localOrigemInfeccao.getCodigoOrigem());
		
		List<Integer> listaMedidas = this.mciMvtoMedidaPreventivasDAO.
				listarMvtosMedidasPreventivasPorUnfSeqTipoOrigem(localOrigemInfeccao.getUnfSeq(), localOrigemInfeccao.getCodigoOrigem());
		
		if (!listaTopografias.isEmpty() || !listaMedidas.isEmpty()) {
			msgErroExclusao = "Local de Origem não pode ser excluído porque está associado:";
			
			if (!listaTopografias.isEmpty()) {
				msgErroExclusao = msgErroExclusao.concat("\n à(s) prescrições: ");
				for (Integer seq : listaTopografias) {
					msgErroExclusao = msgErroExclusao.concat("\n").concat(seq.toString());
				}
			}
			if (!listaMedidas.isEmpty()) {
				msgErroExclusao = msgErroExclusao.concat("\n à(s) notificações: ");
				for (Integer seq : listaMedidas) {
					msgErroExclusao = msgErroExclusao.concat("\n").concat(seq.toString());
				}
			}
			return msgErroExclusao;
		}
		this.mciLocalEtiologiaON.excluirMciLocalEtiologia(localEtiologia);
		return null;
	}
}
