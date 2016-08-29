package br.gov.mec.aghu.controleinfeccao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciCidNotificacaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciItemCondicaoClinicaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciParamTopogProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciCidNotificacao;
import br.gov.mec.aghu.model.MciItemCondicaoClinica;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciParamTopogProcedimento;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TopografiaPorProcedimentoON extends BaseBusiness {
	
	private static final long serialVersionUID = -1578601633154535873L;
	private static final Log LOG = LogFactory.getLog(TopografiaPorProcedimentoON.class);

	public enum TopografiaPorProcedimentoONExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH, MSG_TOPO_INFE_PERIODO, MSG_TOPO_PROC_RESTRICAO_EXCLUSAO, MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_PARAMETRO, 
		MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_NOTIFICACOES, MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_ITENS_DE_CONDICAO_CLINICA,
		MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_NOTIFICACOES_CID;
	}
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	private MciTopografiaProcedimentoRN topografiaProcedimentoRN; 
	
	@Inject
	private MciTopografiaProcedimentoDAO topografiaProcedimentoDAO;
	
	@Inject
	private MciTopografiaInfeccaoDAO topografiaInfeccaoDAO;
	
	@Inject
	private MciCidNotificacaoDAO notificacaoDAO;
	
	@Inject
	private MciItemCondicaoClinicaDAO itemCondicaoClinicaDAO;
	
	@Inject
	private MciParamTopogProcedimentoDAO paramTopogProcedimentoDAO;
	
	@Inject
	private MciMvtoInfeccaoTopografiasDAO mvtoInfeccaoTopografiasDAO;

	@Inject
	private RapServidoresDAO servidoresDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	public List<TopografiaProcedimentoVO> listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seq, String descricao,
			DominioSituacao situacao, Short toiSeq) {
		
		List<TopografiaProcedimentoVO> lista = topografiaProcedimentoDAO.
				listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(firstResult, maxResult, orderProperty, asc, seq, descricao, situacao, toiSeq);
		
		formatarVO(lista);

		return lista;
	}

	private void formatarVO(List<TopografiaProcedimentoVO> lista) {
		if(lista != null){
			for (TopografiaProcedimentoVO vo : lista) {
				vo.setSituacaoBoolean(vo.getIndSituacao().isAtivo());
			}
		}
	}

	public Long listarMciTopografiaProcedimentoPorSeqDescSitSeqTopCount(
			Short seq, String descricao, DominioSituacao situacao, Short toiSeq) {
		return topografiaProcedimentoDAO
				.listarMciTopografiaProcedimentoPorSeqDescSitSeqTopCount(seq,
						descricao, situacao, toiSeq);
	}

	public void persistirTopografiaProcedimento(TopografiaProcedimentoVO vo) throws ApplicationBusinessException {
		controleInfeccaoRN.notNull(vo, TopografiaPorProcedimentoONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		topografiaProcedimentoRN.persistir(getMciTopografiaInfeccao(vo));
	}
	
	public void excluirTopografiaProcedimentoVO(TopografiaProcedimentoVO vo) throws BaseException {
		controleInfeccaoRN.notNull(vo, TopografiaPorProcedimentoONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		controleInfeccaoRN.validarNumeroDiasDecorridosCriacaoRegistro(vo.getCriadoEm(),TopografiaPorProcedimentoONExceptionCode.MSG_TOPO_INFE_PERIODO);
		verificarExclusaoEncerramentoOperacao(vo.getSeq());
		topografiaProcedimentoRN.remover(vo.getSeq());
	}

	public List<TopografiaInfeccaoVO> suggestionBoxTopografiaInfeccaoPorSeqOuDescricao(String strPesquisa) {
		return topografiaInfeccaoDAO.suggestionBoxTopografiaInfeccaoPorSeqOuDescricao(strPesquisa, DominioSituacao.A);
	}
	
	private MciTopografiaProcedimento getMciTopografiaInfeccao(TopografiaProcedimentoVO vo) {
		MciTopografiaProcedimento topografiaProcedimento = new MciTopografiaProcedimento();
		
		topografiaProcedimento.setSeq(vo.getSeq());
		topografiaProcedimento.setDescricao(vo.getDescricao());
		topografiaProcedimento.setIndSituacao(DominioSituacao.getInstance(vo.getSituacaoBoolean()));
		topografiaProcedimento.setIndPermSobreposicao(Boolean.TRUE);
		topografiaProcedimento.setCriadoEm(vo.getCriadoEm());
		RapServidores servidor =  servidoresDAO.obterPorChavePrimaria(new RapServidoresId(vo.getServidorMatricula(), vo.getServidorVinCodigo()));
		topografiaProcedimento.setServidor(servidor);
		
		if(vo.getSeqTopografiaInfeccao() != null) {
			topografiaProcedimento.setTopografiaInfeccao(topografiaInfeccaoDAO.obterPorChavePrimaria(vo.getSeqTopografiaInfeccao()));
		}

		if(vo.getSerMatriculaMovimentado() != null && vo.getSerVinCodigoMovimentado() != null){
			topografiaProcedimento.setAlteradoEm(vo.getAlteradoEm());
			RapServidores movimentadoPor = servidoresDAO.obterPorChavePrimaria(new RapServidoresId(vo.getSerMatriculaMovimentado(), vo.getSerVinCodigoMovimentado()));
			topografiaProcedimento.setMovimentadoPor(movimentadoPor);
		}
		
		return topografiaProcedimento;
	}
	
	private void verificarExclusaoEncerramentoOperacao(Short id) throws BaseException {

		boolean habilitarException = false;
		BaseListException listaDeErros = new BaseListException();
		listaDeErros.add(new ApplicationBusinessException(TopografiaPorProcedimentoONExceptionCode.MSG_TOPO_PROC_RESTRICAO_EXCLUSAO));
		
		List<MciParamTopogProcedimento> listaParamTopogInfeccao =  paramTopogProcedimentoDAO.listarPorTopSeq(id);
		if(existeRegistros(listaParamTopogInfeccao)){
			habilitarException =  true;
			for (MciParamTopogProcedimento paramTopogProcedimento : listaParamTopogInfeccao) {
				listaDeErros.add(new ApplicationBusinessException(TopografiaPorProcedimentoONExceptionCode.MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_PARAMETRO,
						+ paramTopogProcedimento.getId().getPruSeq() + " - " + id.toString()));
			}
		}
		
		List<MciMvtoInfeccaoTopografias> listaMvtoInfeccaoTopografias = mvtoInfeccaoTopografiasDAO.listarPorTopSeq(id);
		if(existeRegistros(listaMvtoInfeccaoTopografias)){
			habilitarException =  true;
			for (MciMvtoInfeccaoTopografias mvtoInfeccaoTopografia : listaMvtoInfeccaoTopografias) {
				listaDeErros.add(new ApplicationBusinessException(TopografiaPorProcedimentoONExceptionCode.MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_NOTIFICACOES_CID, 
						mvtoInfeccaoTopografia.getSeq()));
			}
		}
		
		List<MciCidNotificacao> lisatNotificacao = notificacaoDAO.listarPorTopSeq(id);
		if(existeRegistros(lisatNotificacao)){
			habilitarException =  true;
			for (MciCidNotificacao mciCidNotificacao : lisatNotificacao) {
				listaDeErros.add(new ApplicationBusinessException(
								TopografiaPorProcedimentoONExceptionCode.MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_NOTIFICACOES, mciCidNotificacao.getSeq()));
			}
		}
		
		List<MciItemCondicaoClinica> listaItemCondicaoClinica = itemCondicaoClinicaDAO.listarPorTopSeq(id);
		if(existeRegistros(listaItemCondicaoClinica)){
			habilitarException =  true;
			for (MciItemCondicaoClinica itemCondicaoClinica : listaItemCondicaoClinica) {
				listaDeErros
						.add(new ApplicationBusinessException(
								TopografiaPorProcedimentoONExceptionCode.MSG_TOPO_PROC_RESTRICAO_EXCLUSAO_ITENS_DE_CONDICAO_CLINICA,
								itemCondicaoClinica.getSeq()));
			}
		}
		
		if (habilitarException && listaDeErros.hasException()) {
			throw listaDeErros;
		}
	}

	private boolean existeRegistros(List<?> list){
		return (list != null && !list.isEmpty()) ? true : false; 
	}

}
