package br.gov.mec.aghu.exames.pesquisa.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemSolicExameHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.PesquisaExameDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PesquisaExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PesquisaExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private PesquisaExameDAO pesquisaExameDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7378495636321793970L;

	public List<AelAgrpPesquisas> buscaAgrupamentosPesquisa(Object pesquisa) {
		return getPesquisaExameDAO().buscaAgrupamentosPesquisa(pesquisa);
	}

	public List<AinLeitos> obterLeitosAtivosPorUnf(Object pesquisa, Short unfSeq) {
		return getInternacaoFacade().obterLeitosAtivosPorUnf(pesquisa, unfSeq);
	}
	
	public Long obterLeitosAtivosPorUnfCount(Object pesquisa, Short unfSeq) {
		return getInternacaoFacade().obterLeitosAtivosPorUnfCount(pesquisa, unfSeq);
	}
	
	public List<VAelExamesSolicitacao> obterNomeExames(Object objPesquisa) {
		return getPrescricaoMedicaFacade().obterNomeExames(objPesquisa);
	}
	
	public Long obterNomeExamesCount(Object objPesquisa) {
		return getPrescricaoMedicaFacade().obterNomeExamesCount(objPesquisa);
	}
	
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorSolicExa(Integer seq_solicitacao){
		 return getPesquisaExameDAO().buscarAipPacientesPorSolicExa(seq_solicitacao);
	}
	
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumeroAp(Long numeroAp, AelConfigExLaudoUnico configExame){
		 return getPesquisaExameDAO().buscarAipPacientesPorNumeroAp(numeroAp, configExame);
	}
	
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorConsulta(Integer seq_consulta) {
		return getPesquisaExameDAO().buscarAipPacientesPorConsulta(seq_consulta);
	}

	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorServidor(Integer matricula, Short vinCodigo) {
		return getPesquisaExameDAO().buscarAipPacientesPorServidor(matricula, vinCodigo);
	}

	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorParametros(Integer prontuarioPac, String nomePaciente, AinLeitos leitoPac, AghUnidadesFuncionais unidadeFuncionalPac){
		return getPesquisaExameDAO().buscarAipPacientesPorParametros(prontuarioPac, nomePaciente, leitoPac, unidadeFuncionalPac);
	}
	
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPacienteHist(AipPacientes paciente) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
		
		filtro.setServidorLogado(servidorLogado);
		filtro.setProntuarioPac(paciente.getProntuario());

		buscaSituacaoParametroSistema(filtro);
		
		List<PesquisaExamesPacientesResultsVO> list = getAelItemSolicExameHistDAO()
			.buscaExamesSolicitadosPorPacienteHist(paciente.getCodigo(), null, filtro);
		
		return list;
	}


	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPaciente(Integer codigo, Integer seq_consulta, PesquisaExamesFiltroVO filtro) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		filtro.setServidorLogado(servidorLogado);

		buscaSituacaoParametroSistema(filtro);
		
		List<PesquisaExamesPacientesResultsVO> result = getAelItemSolicitacaoExameDAO().buscaExamesSolicitadosPorPaciente(codigo, seq_consulta, filtro);
		
		return result;
	}

	
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorSolicitante(PesquisaExamesFiltroVO filtro) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		filtro.setServidorLogado(servidorLogado);

		buscaSituacaoParametroSistema(filtro);

		List<PesquisaExamesPacientesResultsVO> result = getPesquisaExameDAO().buscaExamesSolicitadosPorSolicitante(filtro);
		
		return result;		
	}
	
	
	public List<RapServidores> obterServidorSolic(String objPesquisa) {
		
		Integer diasPermitidos = this.obterDiasPermitidos();
		
		return this.getRegistroColaboradorFacade()
			.pesquisarServidoresSolicitacaoExame(objPesquisa, diasPermitidos); 
	}
	
	public Integer obterServidorSolicCount(String objPesquisa) {
		
		Integer diasPermitidos = this.obterDiasPermitidos();
		
		return this.getRegistroColaboradorFacade()
			.pesquisarServidoresSolicitacaoExameCount(objPesquisa, diasPermitidos); 
	}

	
	private Integer obterDiasPermitidos() {
		Integer diasPermitidos = null;

		try {

			AghParametros param = this.getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_DIAS_SOL_EX_FIM_VINCULO_PERMITIDO);
			diasPermitidos = param.getVlrNumerico().intValue();

		} catch (ApplicationBusinessException e) {
			// Se o parametro nao foi criado / configurado. Entao usa o valor
			// default do metodo de pesquisa.
			diasPermitidos = null;
		}

		return diasPermitidos;
	}
	
	/**
	 * @ORADB V_AEL_EXAMES_ATD_DIVERSOS
	 * @param filtro
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VAelExamesAtdDiversosVO> buscarVAelExamesAtdDiversos(final VAelExamesAtdDiversosFiltroVO filtro) {
		final List<VAelExamesAtdDiversosVO> retorno = this.getPesquisaExameDAO().buscarVAelExamesAtdDiversos(filtro);
		if (retorno == null || retorno.isEmpty()) {
			return new ArrayList<VAelExamesAtdDiversosVO>(0);
		}
		
		final IExamesFacade examesFacade = getExamesFacade();
		final IPesquisaExamesFacade pesquisaExamesFacade = getPesquisaExamesFacade();
		final List<VAelExamesAtdDiversosVO> retornoFiltrado = new ArrayList<VAelExamesAtdDiversosVO>(retorno.size());
		
		if (filtro.isSomenteLaboratorial()) {
			for (final VAelExamesAtdDiversosVO vAelExamesAtdDiversosVO : retorno) {
				if (DominioSimNao.N.equals(this.getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(vAelExamesAtdDiversosVO.getUfeSeq(),
						ConstanteAghCaractUnidFuncionais.AREA_FECHADA))) {
					setarValoresVO(vAelExamesAtdDiversosVO, examesFacade, pesquisaExamesFacade);
					retornoFiltrado.add(vAelExamesAtdDiversosVO);
				}
			}
		} else {
			for (final VAelExamesAtdDiversosVO vAelExamesAtdDiversosVO : retorno) {
				setarValoresVO(vAelExamesAtdDiversosVO, examesFacade, pesquisaExamesFacade);
			}
			retornoFiltrado.addAll(retorno);
		}
		
		// Realiza group by pelos campos do VO
		return new ArrayList<VAelExamesAtdDiversosVO>(CollectionUtils.select(retornoFiltrado, PredicateUtils.uniquePredicate()));
	}
	
	
	private void setarValoresVO(final VAelExamesAtdDiversosVO vAelExamesAtdDiversosVO, final IExamesFacade examesFacade,
			final IPesquisaExamesFacade pesquisaExamesFacade) {
		final AelSolicitacaoExames solicitacaoExames = examesFacade.obterAelSolicitacaoExamePorChavePrimaria(vAelExamesAtdDiversosVO.getSoeSeq());
		vAelExamesAtdDiversosVO.setProntuario(examesFacade.buscarLaudoProntuarioPaciente(solicitacaoExames));
		vAelExamesAtdDiversosVO.setNomePaciente(examesFacade.buscarLaudoNomePaciente(solicitacaoExames));
		vAelExamesAtdDiversosVO.setSexoPaciente(examesFacade.obterLaudoSexoPaciente(solicitacaoExames));
		vAelExamesAtdDiversosVO.setOrigem(pesquisaExamesFacade.validaLaudoOrigemPaciente(solicitacaoExames));
		vAelExamesAtdDiversosVO.setIdade(examesFacade.laudoIdadePaciente(vAelExamesAtdDiversosVO.getSoeSeq()));
		vAelExamesAtdDiversosVO.setPacCodigo(examesFacade.buscarLaudoCodigoPaciente(solicitacaoExames));
	}
	
	private void buscaSituacaoParametroSistema(PesquisaExamesFiltroVO filtro) throws ApplicationBusinessException {
		//parametro P_SITUACAO_PENDENTE
		AghParametrosVO pSituacaoPendente = new AghParametrosVO();
		pSituacaoPendente.setNome(AghuParametrosEnum.P_SITUACAO_PENDENTE.toString());
		getParametroFacade().getAghpParametro(pSituacaoPendente);
		filtro.setpSituacaoPendente(pSituacaoPendente.getVlrTexto());

		////parametro P_SITUACAO_PENDENTE
		AghParametrosVO pSituacaoCancelado = new AghParametrosVO();
		pSituacaoCancelado.setNome(AghuParametrosEnum.P_SITUACAO_CANCELADO.toString());
		getParametroFacade().getAghpParametro(pSituacaoCancelado);
		filtro.setpSituacaoCancelado(pSituacaoCancelado.getVlrTexto());
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected PesquisaExameDAO getPesquisaExameDAO(){
		return pesquisaExameDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelItemSolicExameHistDAO getAelItemSolicExameHistDAO() {
		return aelItemSolicExameHistDAO;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected IPesquisaExamesFacade getPesquisaExamesFacade() {
		return pesquisaExamesFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	private IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public List<Short> buscaExamesSolicitadosOrdenados(Integer solicitacao, List<Short> seqps, Boolean isHist) {

		if (isHist) {
			return getPesquisaExameDAO().buscaExamesSolicitadosOrdenadosHist(
					solicitacao, seqps);
		} else {
			return getPesquisaExameDAO().buscaExamesSolicitadosOrdenados(
					solicitacao, seqps);
		}
	}
}
