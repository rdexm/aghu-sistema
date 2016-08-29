package br.gov.mec.aghu.paciente.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarioJn;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacienteProntuario;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.exception.PacienteExceptionCode;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioJnDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitantesProntuarioDAO;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuariosVO;

@Stateless
public class MovimentacaoProntuarioON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MovimentacaoProntuarioON.class);
	@Inject
	private AipMovimentacaoProntuarioJnDAO aipMovimentacaoProntuarioJnDAO;
	@EJB
	private ICadastrosBasicosPacienteFacade adastrosBasicosPacienteFacade;
	@Inject
	private AipPacienteProntuarioDAO aipPacienteProntuarioDAO;
	@Inject
	private AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO;
	@Inject
	private AipSolicitantesProntuarioDAO aipSolicitantesProntuarioDAO;
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private enum MovimentacaoProntuarioONCode implements BusinessExceptionCode {
		ERRO_ORIGEM_NAO_DEFINIDA;
	}
	
	private static final long serialVersionUID = -5407736502420434915L;
	
	public List<AipPacientes> pesquisaPacientesMovimentacaoProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		return getAipMovimentacaoProntuarioDAO().pesquisa(firstResult, maxResults,
				orderProperty, asc, codigoPaciente, prontuario, nomePesquisaPaciente);
	}

	public List<AipSolicitantesProntuario> pesquisarUnidadesSolicitantesPorCodigoOuSigla(
			final Object objPesquisa) {
		
		final String strPesquisa = (String) objPesquisa;

		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroInteger(strPesquisa)) {
			final List<AipSolicitantesProntuario> list = this.getAipSolicitantesProntuariosDAO().pesquisarUnidadeSolicitantePorCodigoESigla(
					Short.valueOf(strPesquisa), null);

			if (list.size() > 0) {
				return list;
			}
		} else {
			final List<AipSolicitantesProntuario> list = this.getAipSolicitantesProntuariosDAO().pesquisarUnidadeSolicitantePorCodigoESigla(null, strPesquisa);

			if (list.size() > 0) {
				return list;
			}
		}

		return null;
	}

	public void persistirVinculoMovimentacaoOrigemProntuario(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados, AghSamis origemProntuario,
			RapServidores servidorLogado) throws ApplicationBusinessException{
		if (origemProntuario == null) {
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.ERRO_ORIGEM_NAO_DEFINIDA);
		}
		for (AipMovimentacaoProntuariosVO itemSelecionado : listaItensSelecionados) {
			validaSeProntuarioArquivado(itemSelecionado);
			popularAipMovimentacaoProntuarioJN(servidorLogado, itemSelecionado);
			alterarVinculoOrigemProntuario(origemProntuario, itemSelecionado);
		}
	}

	private void alterarVinculoOrigemProntuario(AghSamis origemProntuario,
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		AipMovimentacaoProntuarios aipMovimentacaoProntuarios = getAipMovimentacaoProntuarioDAO().listarMovimentacoesProntuariosPorCodigoPaciente(itemSelecionado.getPacCodigo()).get(0);
		if(aipMovimentacaoProntuarios.getLocalAtual() == null || (aipMovimentacaoProntuarios.getSamisOrigem() != null) && (aipMovimentacaoProntuarios.getLocalAtual().equals(aipMovimentacaoProntuarios.getSamisOrigem().getDescricao()))){
			aipMovimentacaoProntuarios.setLocalAtual(origemProntuario.getDescricao());
		}
		aipMovimentacaoProntuarios.setSamisOrigem(origemProntuario);
		this.alterarMovimentacao(aipMovimentacaoProntuarios);
		
		AipPacienteProntuario aipPacienteProntuario = getAipPacienteProntuarioDAO().obterPorChavePrimaria(itemSelecionado.getProntuario());
		aipPacienteProntuario.setSamis(origemProntuario);
		this.alterarPacienteProntuario(aipPacienteProntuario);
	}

	private void alterarPacienteProntuario(AipPacienteProntuario aipPacienteProntuario) throws ApplicationBusinessException {
		this.getAipPacienteProntuarioDAO().atualizar(aipPacienteProntuario);
		
	}

	private void validaSeProntuarioArquivado(
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		if( (itemSelecionado.getOrigemProntuario() != null) && (itemSelecionado.getLocalAtual() != null) && ! ( itemSelecionado.getOrigemProntuario().equals(itemSelecionado.getLocalAtual() ) )){
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_PRONTUARIO_NAO_ARQUIVADO);
		}
	}

	private void popularAipMovimentacaoProntuarioJN(
			RapServidores servidorLogado,
			AipMovimentacaoProntuariosVO itemSelecionado) {
		AipMovimentacaoProntuarioJn aipMovimentacaoProntuarioJn = new AipMovimentacaoProntuarioJn();
		AipSolicitantesProntuario solicitanteProntuario = getCadastrosBasicosPacienteFacade().obterSolicitanteProntuario(itemSelecionado.getCodigoSolicitante());
		aipMovimentacaoProntuarioJn.setSeq(itemSelecionado.getSeq());
		aipMovimentacaoProntuarioJn.setObservacoes(itemSelecionado.getObservacoes());
		aipMovimentacaoProntuarioJn.setVolumes(itemSelecionado.getVolumes());
		aipMovimentacaoProntuarioJn.setTipoEnvio(itemSelecionado.getTipoEnvio());
		aipMovimentacaoProntuarioJn.setSituacao(itemSelecionado.getSituacao());
		aipMovimentacaoProntuarioJn.setDataMovimento(itemSelecionado.getDataMovimentacao());
		aipMovimentacaoProntuarioJn.setDataRetirada(itemSelecionado.getDataRetirada());
		aipMovimentacaoProntuarioJn.setDataDevolucao(itemSelecionado.getDataDevolucao());
		aipMovimentacaoProntuarioJn.setPacCodigo(itemSelecionado.getPacCodigo());
		aipMovimentacaoProntuarioJn.setSerMatricula(itemSelecionado.getServidorMatricula());
		aipMovimentacaoProntuarioJn.setSerVinCodigo(itemSelecionado.getSerVinCodigo());
		aipMovimentacaoProntuarioJn.setSerMatriculaRetirado(itemSelecionado.getSerMatriculaRetirado());
		aipMovimentacaoProntuarioJn.setSerVinCodigoRetirado(itemSelecionado.getSerVinCodigoRetirado());
		aipMovimentacaoProntuarioJn.setSopSeq(solicitanteProntuario.getSeq());
		aipMovimentacaoProntuarioJn.setSlpCodigo(itemSelecionado.getSlpCodigo());
		aipMovimentacaoProntuarioJn.setLocal(itemSelecionado.getLocalPrimeiraMovimentacao());
		aipMovimentacaoProntuarioJn.setLocalAtual(itemSelecionado.getLocalAtual());
		aipMovimentacaoProntuarioJn.setSamisSeq(itemSelecionado.getSeqOrigemProntuario());
		aipMovimentacaoProntuarioJn.setDataCadastroOrigem(itemSelecionado.getDataCadastroOrigemProntuario());
		aipMovimentacaoProntuarioJn.setNomeUsuario(servidorLogado.getUsuario());
		aipMovimentacaoProntuarioJn.setOperacao(DominioOperacoesJournal.UPD);
		this.persistirHistoricoMovimentacaoProntuarioJN(aipMovimentacaoProntuarioJn);
		
	}

	private void persistirHistoricoMovimentacaoProntuarioJN(AipMovimentacaoProntuarioJn aipMovimentacaoProntuarioJn){
		getAipMovimentacaoProntuarioJNDAO().persistir(aipMovimentacaoProntuarioJn);
	}

	private void alterarMovimentacao(AipMovimentacaoProntuarios aipMovimentacaoProntuarios) throws ApplicationBusinessException{
		this.getAipMovimentacaoProntuarioDAO().atualizar(aipMovimentacaoProntuarios);
	}

	public List<AipMovimentacaoProntuariosVO> pesquisaMovimentacoesDeProntuarios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AipPacientes paciente,
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) throws ApplicationBusinessException {
		List<AipMovimentacaoProntuariosVO> listaMovimetacoesProntuario = getAipMovimentacaoProntuarioDAO().
				pesquisaMovimentacoesDeProntuarios(firstResult, maxResult, 
				orderProperty, asc, paciente, origemProntuariosPesquisa, unidadeSolicitantePesquisa, 
				situacao, dataMovimentacaoConsulta);
		if ((listaMovimetacoesProntuario.isEmpty()) && (paciente != null)) {
			listaMovimetacoesProntuario = aipPacientesDAO.pesquisaMovimentacoesDeProntuariosPaciente(paciente);
		}
		return listaMovimetacoesProntuario;
	}

	public Long pesquisaMovimentacoesDeProntuariosCount(
			AipPacientes paciente, 
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) {
		Long valorCountPesquisa = getAipMovimentacaoProntuarioDAO().pesquisaMovimentacoesDeProntuariosCount(paciente, 
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
		if ((valorCountPesquisa == null || valorCountPesquisa == 0) && (paciente != null)) {
			valorCountPesquisa = aipPacientesDAO.pesquisaMovimentacoesDeProntuariosPacienteCount(paciente.getCodigo(), paciente.getProntuario(), 
					paciente.getNome());
		}
		return valorCountPesquisa; 
	}
	
	public List<AipMovimentacaoProntuariosVO> pesquisarTodasMovimentacoesParaSelecionarTodas(
			AipPacientes paciente, 
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) throws ApplicationBusinessException {
		return getAipMovimentacaoProntuarioDAO().pesquisarTodasMovimentacoesParaSelecionarTodas(paciente, 
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
	}

	public void persistirVinculoMovimentacaoUnidadeSolicitante(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			AghUnidadesFuncionais unidadeSolicitanteAlteracao,
			RapServidores servidorLogado, DominioSituacaoMovimentoProntuario situacao) throws ApplicationBusinessException{
		
		validaMovimentoSimultaneoDeDoisProntuarioIguais(listaItensSelecionados);
		
		for (AipMovimentacaoProntuariosVO itemSelecionado : listaItensSelecionados) {
			
			validaSeProntuarioPossuiOrigem(itemSelecionado);
			validaSituacaoDoProntuario(itemSelecionado);
			
			popularAipMovimentacaoProntuarioJN(servidorLogado, itemSelecionado);
			
			AipMovimentacaoProntuarios aipMovimentacaoProntuarios = getAipMovimentacaoProntuarioDAO().listarMovimentacoesProntuariosPorCodigoPaciente(itemSelecionado.getPacCodigo()).get(0);
			aipMovimentacaoProntuarios.setLocalAtual(unidadeSolicitanteAlteracao.getDescricao());
			aipMovimentacaoProntuarios.setSituacao(situacao);
			this.alterarMovimentacao(aipMovimentacaoProntuarios);
		}
	}

	private void validaMovimentoSimultaneoDeDoisProntuarioIguais(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados)
			throws ApplicationBusinessException {
		for (AipMovimentacaoProntuariosVO itemSelecionado1 : listaItensSelecionados) {
			for (AipMovimentacaoProntuariosVO itemSelecionado2 : listaItensSelecionados) {
				if(itemSelecionado1.getPacCodigo().equals(itemSelecionado2.getPacCodigo()) && !(itemSelecionado1.getLocalPrimeiraMovimentacao().equals(itemSelecionado2.getLocalPrimeiraMovimentacao())) ){
					throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_MOV_PRONT_PARA_LOCAIS_DISTINTOS);
				}
			}
		}
	}

	private void validaSituacaoDoProntuario(
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		if( !(itemSelecionado.getSituacao().equals(DominioSituacaoMovimentoProntuario.R)) && !(itemSelecionado.getSituacao().equals(DominioSituacaoMovimentoProntuario.Q))   ){
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_SITUACAO_PRONTUARIO_NAO_PERMITE_MOVIMENTO);
		}
	}

	private void validaSeProntuarioPossuiOrigem(
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		if( itemSelecionado.getSeqOrigemProntuario() == null  ){
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_PRONTUARIO_SEM_ORIGEM);
		}
	}

	public void persistirDevolucaoDeProntuario(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			RapServidores servidorLogado) throws ApplicationBusinessException{
		for (AipMovimentacaoProntuariosVO itemSelecionado : listaItensSelecionados) {
			if( itemSelecionado.getOrigemProntuario() == null || ( itemSelecionado.getOrigemProntuario().equals(itemSelecionado.getLocalAtual() ) )){
				throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_PRONTUARIO_NAO_PODE_SER_DEVOLVIDO);
			}
			popularAipMovimentacaoProntuarioJN(servidorLogado, itemSelecionado);
			
			AipMovimentacaoProntuarios aipMovimentacaoProntuarios = getAipMovimentacaoProntuarioDAO().listarMovimentacoesProntuariosPorCodigoPaciente(itemSelecionado.getPacCodigo()).get(0);
			aipMovimentacaoProntuarios.setLocalAtual(aipMovimentacaoProntuarios.getSamisOrigem().getDescricao());
			aipMovimentacaoProntuarios.setSituacao(DominioSituacaoMovimentoProntuario.D);
			aipMovimentacaoProntuarios.setDataDevolucao(new Date());
			this.alterarMovimentacao(aipMovimentacaoProntuarios);
		}
	}
	
	
	public List<AipSolicitantesProntuario> verificaLocalParaMovimentacao(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			 Object param) throws ApplicationBusinessException {
			for (AipMovimentacaoProntuariosVO itemSelecionado : listaItensSelecionados) {
				if( (itemSelecionado.getOrigemProntuario() == null) || (itemSelecionado.getLocalAtual().equals(itemSelecionado.getOrigemProntuario()) && itemSelecionado.getSituacao().getDescricao().equals(DominioSituacaoMovimentoProntuario.Q.getDescricao()) ) ){
					String localPrimeiraMovimentacaoConcatenada = listaItensSelecionados.get(0).getLocalPrimeiraMovimentacao();
					Object localPrimeiraMovimentacao = localPrimeiraMovimentacaoConcatenada.substring(localPrimeiraMovimentacaoConcatenada.indexOf('/')+1, localPrimeiraMovimentacaoConcatenada.lastIndexOf('/'));
					return pesquisarUnidadesSolicitantesPorCodigoOuSigla(localPrimeiraMovimentacao);
				} else{
					return pesquisarUnidadesSolicitantesPorCodigoOuSigla(param);
				}
			}
			return null;
	}
	
	private AipSolicitantesProntuarioDAO getAipSolicitantesProntuariosDAO() {
		return aipSolicitantesProntuarioDAO;
	}
	
	private AipMovimentacaoProntuarioDAO getAipMovimentacaoProntuarioDAO(){
		return aipMovimentacaoProntuarioDAO;
	}
	
	private AipPacienteProntuarioDAO getAipPacienteProntuarioDAO(){
		return aipPacienteProntuarioDAO;
	}
	
	private ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return adastrosBasicosPacienteFacade;
	}
	
	private AipMovimentacaoProntuarioJnDAO getAipMovimentacaoProntuarioJNDAO(){
		return aipMovimentacaoProntuarioJnDAO;
	}

}
