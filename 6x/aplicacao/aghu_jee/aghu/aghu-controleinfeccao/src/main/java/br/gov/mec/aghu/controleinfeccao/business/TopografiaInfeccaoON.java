package br.gov.mec.aghu.controleinfeccao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciCidNotificacaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciParamTopogInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPatologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciCidNotificacao;
import br.gov.mec.aghu.model.MciParamTopogInfeccao;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
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
public class TopografiaInfeccaoON extends BaseBusiness {

	private static final long serialVersionUID = -1578601633154535873L;
	private static final Log LOG = LogFactory.getLog(TopografiaInfeccaoON.class);
	
	public enum TopografiaInfeccaoONExceptionCode implements
			BusinessExceptionCode {
		MSG_TOPO_INFE_PERIODO, ERRO_P_CCIH_NRO_DIAS_PERM_DEL, ERRO_PERSISTENCIA_CCIH, MSG_TOPO_INFE_RESTRICAO_EXCLUSAO, MSG_TOPO_INFE_DADOS_INCOMPLETOS, MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_PATOLOGIA, MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_NOTIFICACOES, MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_PARAMETRO, MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_PROCEDIMENTO;
	}
	
	@EJB
	private MciTopografiaInfeccaoRN mciTopografiaInfeccaoRN;
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;

	@Inject
	private MciCidNotificacaoDAO mciCidNotificacaoDAO;

	@Inject
	private MciPatologiaInfeccaoDAO mciPatologiaInfeccaoDAO;

	@Inject
	private MciParamTopogInfeccaoDAO mciParamTopogInfeccaoDAO;

	@Inject
	private MciTopografiaProcedimentoDAO mciTopografiaProcedimentoDAO;
	
	@Inject
	private RapServidoresDAO servidoresDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MciTopografiaInfeccaoDAO mciTopografiaInfeccaoDAO;

	public Long listarMciTopografiaInfeccaoPorDescricaoESituacaoCount(
			String descricao, DominioSituacao situacao) {
		
		return mciTopografiaInfeccaoDAO
				.listarMciTopografiaInfeccaoPorDescricaoESituacaoCount(
						descricao, situacao);
	}

	public List<TopografiaInfeccaoVO> listarMciTopografiaInfeccaoPorDescricaoESituacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String descricao, DominioSituacao situacao) {

		List<TopografiaInfeccaoVO> lista = mciTopografiaInfeccaoDAO
				.listarMciTopografiaInfeccaoPorDescricaoESituacao(firstResult,
						maxResult, orderProperty, asc, descricao, situacao);

		formatarVO(lista);

		return lista;
	}

	private void formatarVO(List<TopografiaInfeccaoVO> lista) {
		if(lista != null){
			for (TopografiaInfeccaoVO vo : lista) {
				vo.setSituacaoBoolean(vo.getSituacao().isAtivo());
				vo.setCriadoAlteradoEm(vo.getAlteradoEm() == null ? vo.getCriadoEm() : vo.getAlteradoEm());
			}
		}
	}

	public void persistirTopografiaInfeccao(TopografiaInfeccaoVO vo) throws ApplicationBusinessException {
		controleInfeccaoRN.notNull(vo, TopografiaInfeccaoONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		validarDescricao(vo.getDescricao());
		mciTopografiaInfeccaoRN.persistir(getMciTopografiaInfeccao(vo));
	}

	private void validarDescricao(String descricao) throws ApplicationBusinessException {
		if (descricao == null || StringUtils.isEmpty(descricao) || StringUtils.isBlank(descricao)){
			throw new ApplicationBusinessException(TopografiaInfeccaoONExceptionCode.MSG_TOPO_INFE_DADOS_INCOMPLETOS);
		}
	}

	public void excluirTopografiaInfeccao(TopografiaInfeccaoVO vo) throws BaseException {
		controleInfeccaoRN.notNull(vo, TopografiaInfeccaoONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		controleInfeccaoRN.validarNumeroDiasDecorridosCriacaoRegistro(vo.getCriadoEm(), TopografiaInfeccaoONExceptionCode.MSG_TOPO_INFE_PERIODO);
		verificarExclusaoEncerramentoOperacao(vo.getSeq());
		mciTopografiaInfeccaoRN.remover(vo.getSeq());		
	}
	
	private MciTopografiaInfeccao getMciTopografiaInfeccao(TopografiaInfeccaoVO vo) {
		MciTopografiaInfeccao mciTopografiaInfeccao = new MciTopografiaInfeccao();
		
		mciTopografiaInfeccao.setSeq(vo.getSeq());
		mciTopografiaInfeccao.setDescricao(vo.getDescricao());
		mciTopografiaInfeccao.setSupervisao(vo.getSupervisao());
		mciTopografiaInfeccao.setSituacao(DominioSituacao.getInstance(vo.getSituacaoBoolean()));
		mciTopografiaInfeccao.setPacienteInfectado(vo.getPacienteInfectado());
		mciTopografiaInfeccao.setContaInfecadoMensal(vo.getContaInfecadoMensal());
		mciTopografiaInfeccao.setCriadoEm(vo.getCriadoEm());
		mciTopografiaInfeccao.setAlteradoEm(vo.getAlteradoEm());
		
		RapServidores servidor =  servidoresDAO.obterPorChavePrimaria(new RapServidoresId(vo.getMatricula(), vo.getVinCodigo()));
		mciTopografiaInfeccao.setServidor(servidor);
		
		if(vo.getMatriculaMovi() != null && vo.getVinCodigoMovi() != null){
			RapServidores movimentadoPor = servidoresDAO.obterPorChavePrimaria(new RapServidoresId(vo.getMatriculaMovi(), vo.getVinCodigoMovi()));
			mciTopografiaInfeccao.setMovimentadoPor(movimentadoPor);
		}
		
		return mciTopografiaInfeccao;
	}

	/**
	 * Testar se existem patologias, procedimentos, parâmetros de relatório ou
	 * notificações associados a esta topografia selecionada [C3] Caso positivo
	 * exibir mensagem MENSAGEM_Restricao_Exclusao e encerrar operação
	 */
	private void verificarExclusaoEncerramentoOperacao(Short id) throws BaseException {

		boolean habilitarException = false;
		BaseListException listaDeErros = new BaseListException();
		listaDeErros.add(new ApplicationBusinessException(TopografiaInfeccaoONExceptionCode.MSG_TOPO_INFE_RESTRICAO_EXCLUSAO));
		
		List<MciCidNotificacao> lisatNotificacao = mciCidNotificacaoDAO.listarPorToiSeq(id);
		if(existeRegistros(lisatNotificacao)){
			habilitarException =  true;
			for (MciCidNotificacao mciCidNotificacao : lisatNotificacao) {
				listaDeErros.add(new ApplicationBusinessException(TopografiaInfeccaoONExceptionCode.MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_NOTIFICACOES, 
						mciCidNotificacao.getSeq()));
			}
		}
		
		List<MciPatologiaInfeccao> listaPatologiaInfeccao = mciPatologiaInfeccaoDAO.listarPorToiSeq(id);
		if(existeRegistros(listaPatologiaInfeccao)){
			habilitarException =  true;
			for (MciPatologiaInfeccao patologiaInfeccao : listaPatologiaInfeccao) {
				listaDeErros.add(new ApplicationBusinessException(TopografiaInfeccaoONExceptionCode.MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_PATOLOGIA, 
						patologiaInfeccao.getSeq() + " - " + patologiaInfeccao.getDescricao()));
			}
		}
		
		List<MciParamTopogInfeccao> listaParamTopogInfeccao =  mciParamTopogInfeccaoDAO.listarPorToiSeq(id);
		if(existeRegistros(listaParamTopogInfeccao)){
			habilitarException =  true;
			for (MciParamTopogInfeccao paramTopogInfeccao : listaParamTopogInfeccao) {
				listaDeErros.add(new ApplicationBusinessException(TopografiaInfeccaoONExceptionCode.MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_PARAMETRO, 
						paramTopogInfeccao.getId().getPruSeq() + " - " + id.toString()));
			}
		}

		List<MciTopografiaProcedimento> listaTopografiaProcedimento = mciTopografiaProcedimentoDAO.listarPorToiSeq(id);
		if(existeRegistros(listaTopografiaProcedimento)){
			habilitarException =  true;
			for (MciTopografiaProcedimento topografiaProcedimento : listaTopografiaProcedimento) {
				listaDeErros.add(new ApplicationBusinessException(TopografiaInfeccaoONExceptionCode.MSG_TOPO_INFE_RESTRICAO_EXCLUSAO_PROCEDIMENTO, 
						topografiaProcedimento.getSeq() + " - " + topografiaProcedimento.getDescricao()));
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
