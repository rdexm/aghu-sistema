package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.ejb3.annotation.TransactionTimeout;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.EpeCuidadosRN;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoUnf;
import br.gov.mec.aghu.model.EpeCuidadoUnfId;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeDataItemSumario;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.model.EpeUnidPacAtendimento;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoMedicamentoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoUnfDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDataItemSumarioDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelacionadoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeGrupoNecesBasicaDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeNotificacaoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSubgrupoNecesBasicaDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeUnidPacAtendimentoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoMedicamentoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoCuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EpePrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EtiologiaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PacienteEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelSumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SinalSintomaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;

/**
 * Porta de entrada do módulo de prescrição de enfermagem.
 * 
 * @author dansantos
 * 
 */

@Modulo(ModuloEnum.PRESCRICAO_ENFERMAGEM)
@SuppressWarnings({"PMD.CouplingBetweenObjects"})
@Stateless
public class PrescricaoEnfermagemFacade extends BaseFacade implements IPrescricaoEnfermagemFacade {
	
	@EJB
	private ElaboracaoPrescricaoEnfermagemON elaboracaoPrescricaoEnfermagemON;
	
	@EJB
	private ManterCuidadosON manterCuidadosON;
	
	@EJB
	private FinalizacaoPendentePrescricaoEnfermagemRN finalizacaoPendentePrescricaoEnfermagemRN;
	
	@EJB
	private ManterSumarioEnfermagemRN manterSumarioEnfermagemRN;
	
	@EJB
	private ListaPacientesEnfermagemON listaPacientesEnfermagemON;
	
	@EJB
	private ManterSumarioSchedulerRN manterSumarioSchedulerRN;
	
	@EJB
	private ManutencaoPrescricaoCuidadoRN manutencaoPrescricaoCuidadoRN;
	
	@EJB
	private ManutencaoPrescricaoCuidadoON manutencaoPrescricaoCuidadoON;
	
	@EJB
	private SinalSintomaON sinalSintomaON;
	
	@EJB
	private AprazamentoPrescricaoEnfermagemRN aprazamentoPrescricaoEnfermagemRN;
	
	@EJB
	private RelatorioRotinaCuidadoON relatorioRotinaCuidadoON;
	
	@EJB
	private DiagnosticoON diagnosticoON;
	
	@EJB
	private SelecaoPrescricaoEnfermagemON selecaoPrescricaoEnfermagemON;
	
	@EJB
	private ManutencaoPrescricaoEnfermagemON manutencaoPrescricaoEnfermagemON;
	
	@EJB
	private FinalizacaoPrescricaoEnfermagemRN finalizacaoPrescricaoEnfermagemRN;
	
	@EJB
	private ElaboracaoPrescricaoEnfermagemRN elaboracaoPrescricaoEnfermagemRN;
	
	@EJB
	private EpeCuidadosRN epeCuidadosRN;
	
	@EJB
	private EncerramentoDiagnosticoON encerramentoDiagnosticoON;
	
	@EJB
	private VisualizarAnamneseEvolucoesRN visualizarAnamneseEvolucoesRN;
	
	@Inject
	private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;
	
	@Inject
	private EpeUnidPacAtendimentoDAO epeUnidPacAtendimentoDAO;
	
	@Inject
	private EpeCuidadoMedicamentoDAO epeCuidadoMedicamentoDAO;
	
	@Inject
	private EpeCuidadoDiagnosticoDAO epeCuidadoDiagnosticoDAO;
	
	@Inject
	private EpeFatRelacionadoDAO epeFatRelacionadoDAO;
	
	@Inject
	private EpeSubgrupoNecesBasicaDAO epeSubgrupoNecesBasicaDAO;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;
	
	@Inject
	private EpeCuidadoUnfDAO epeCuidadoUnfDAO;
	
	@Inject
	private EpeCuidadosDAO epeCuidadosDAO;
	
	@Inject
	private EpeDiagnosticoDAO epeDiagnosticoDAO;
	
	@Inject
	private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;
	
	@Inject
	private EpeNotificacaoDAO epeNotificacaoDAO;
	
	@Inject
	private EpeGrupoNecesBasicaDAO epeGrupoNecesBasicaDAO;
	
	@Inject
	private EpeFatRelDiagnosticoDAO epeFatRelDiagnosticoDAO;
	
	@Inject
	private EpeDataItemSumarioDAO epeDataItemSumarioDAO;

	private static final long serialVersionUID = 1448628938011873332L;

	@Override
	@BypassInactiveModule
	public List<PacienteEnfermagemVO> listarPacientes(
			final RapServidores servidor) throws BaseException {

		return this.getListaPacientesEnfermagemON().pesquisarListaPacientes(
				servidor);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#verificarCuidadoJaExistePrescricao(java.lang.Short, java.lang.Integer)
	 */
	@Override
	public Boolean verificarCuidadoJaExistePrescricao(Short cuiSeq, EpePrescricaoEnfermagemId penId){
		return this.getEpePrescricoesCuidadosDAO().verificarCuidadoJaExistePrescricao(cuiSeq, penId);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#confirmarPrescricaoEnfermagem(br.gov.mec.aghu.model.EpePrescricaoEnfermagem)
	 */
	@Override
	public void confirmarPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) throws BaseException{
		getFinalizacaoPrescricaoEnfermagemRN().confirmarPrescricaoEnfermagem(prescricaoEnfermagem);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#confirmarPendentePrescricaoEnfermagem(br.gov.mec.aghu.model.EpePrescricaoEnfermagem)
	 */
	@Override
	public void confirmarPendentePrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) throws BaseException{
		getFinalizacaoPendentePrescricaoEnfermagemRN().confirmarPendentePrescricaoEnfermagem(prescricaoEnfermagem);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#clonarPrescricaoEnfermagem(br.gov.mec.aghu.model.EpePrescricaoEnfermagem)
	 */
	@Override
	@BypassInactiveModule
	public EpePrescricaoEnfermagem clonarPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) throws ApplicationBusinessException{
		return getElaboracaoPrescricaoEnfermagemON().clonarPrescricaoEnfermagem(prescricaoEnfermagem);
	}
	
	@Override
	public EpePrescricoesCuidados alterarPrescricaoCuidado (EpePrescricoesCuidados prescricaoCuidadoNew, String descricao, boolean cuidadosAnterioresComErro) throws BaseException{
		return getManutencaoPrescricaoCuidadoON().alterarPrescricaoCuidado(prescricaoCuidadoNew, descricao, cuidadosAnterioresComErro);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#inserirPrescricaoCuidado(br.gov.mec.aghu.model.EpePrescricoesCuidados)
	 */
	@Override
	public void inserirPrescricaoCuidado(EpePrescricoesCuidados prescricaoCuidado, String descricao) throws BaseException{
		getManutencaoPrescricaoCuidadoON().inserirPrescricaoCuidado(prescricaoCuidado, descricao);
	}
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#inserirPrescCuidDiagnostico(br.gov.mec.aghu.model.EpePrescricoesCuidados, br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO)
	 */
	@Override
	public void inserirPrescCuidDiagnostico(EpePrescricoesCuidados prescricaoCuidado, CuidadoVO cuidadoVO){
		getManutencaoPrescricaoCuidadoON().inserirPrescCuidDiagnostico(prescricaoCuidado, cuidadoVO);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#obterAtendimentoAtualPorProntuario(java.lang.Integer)
	 */
	@Override
	public List<AghAtendimentos> obterAtendimentoAtualPorProntuario(Integer prontuario) {
		return getElaboracaoPrescricaoEnfermagemON().obterAtendimentoAtualPorProntuario(prontuario);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#obterUnicoAtendimentoAtualPorProntuario(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AghAtendimentos obterUnicoAtendimentoAtualPorProntuario(Integer prontuario) {
		AghAtendimentos atendimento = null;
		List<AghAtendimentos> listaAtendimentos = getElaboracaoPrescricaoEnfermagemON()
				.obterAtendimentoAtualPorProntuario(prontuario);
		if (listaAtendimentos != null && !listaAtendimentos.isEmpty()) {
			atendimento = listaAtendimentos.get(0);
		}
		return atendimento;
	}	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#obterAtendimentoPorLeito(java.lang.String)
	 */
	@Override
	public AghAtendimentos obterAtendimentoPorLeito(String param) throws ApplicationBusinessException {
		return getElaboracaoPrescricaoEnfermagemON().obterAtendimentoAtualPorLeitoId(param);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarPrescricaoEnfermagemNaoEncerradaPorAtendimento(java.lang.Integer, java.util.Date)
	 */
	@Override
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemNaoEncerradaPorAtendimento(Integer atdSeq, Date dataAtual) {
		return this.getElaboracaoPrescricaoEnfermagemON()
				.pesquisarPrescricaoEnfermagemNaoEncerradaPorAtendimento(atdSeq, dataAtual);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#obterDataReferenciaProximaPrescricao(br.gov.mec.aghu.model.AghAtendimentos, java.util.Date)
	 */
	@Override
	public Date obterDataReferenciaProximaPrescricao(AghAtendimentos atendimento, Date dataAtual) throws ApplicationBusinessException {
		return this.getElaboracaoPrescricaoEnfermagemON().obterDataReferenciaProximaPrescricao(atendimento, dataAtual);
	}	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#editarPrescricao(br.gov.mec.aghu.model.EpePrescricaoEnfermagem, br.gov.mec.aghu.model.EpePrescricaoEnfermagem, java.lang.Boolean)
	 */
	@Override
	public void editarPrescricao(EpePrescricaoEnfermagem prescricaoEnfermagemOld, EpePrescricaoEnfermagem prescricaoEnfermagemNew, Boolean cienteEmUso) throws BaseException {
		this.getElaboracaoPrescricaoEnfermagemON().editarPrescricao(prescricaoEnfermagemOld, prescricaoEnfermagemNew, cienteEmUso);
	}
	
	@Override
	@BypassInactiveModule
	public void atualizarPrescricao(EpePrescricaoEnfermagem prescricaoEnfermagemOld, EpePrescricaoEnfermagem prescricaoEnfermagemNew, boolean flush) throws BaseException{
		getElaboracaoPrescricaoEnfermagemON().atualizarPrescricaoEnfermagem(prescricaoEnfermagemOld, prescricaoEnfermagemNew, flush);
	}
	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#verificarCriarPrescricao(br.gov.mec.aghu.model.AghAtendimentos)
	 */
	@Override
	public void verificarCriarPrescricao(
			final AghAtendimentos atendimento
		) throws ApplicationBusinessException {
		this.getElaboracaoPrescricaoEnfermagemON().verificarCriarPrescricao(atendimento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#criarPrescricao(br.gov.mec.aghu.model.AghAtendimentos, java.util.Date)
	 */
	@Override
	public EpePrescricaoEnfermagem criarPrescricao(
			final AghAtendimentos atendimento, Date dataReferencia) throws BaseException {
		return getElaboracaoPrescricaoEnfermagemON().criarPrescricao(atendimento, dataReferencia);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#buscarDadosCabecalhoPrescricaoEnfermagemVO(br.gov.mec.aghu.model.EpePrescricaoEnfermagemId)
	 */
	@Override
	@BypassInactiveModule
	public PrescricaoEnfermagemVO buscarDadosCabecalhoPrescricaoEnfermagemVO(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId)
			throws ApplicationBusinessException {
		return this.getManutencaoPrescricaoEnfermagemON()
				.buscarDadosCabecalhoPrescricaoEnfermagemVO(prescricaoEnfermagemId);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO(br.gov.mec.aghu.model.EpePrescricaoEnfermagemId)
	 */
	@Override
	@BypassInactiveModule
	public PrescricaoEnfermagemVO buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId)
			throws ApplicationBusinessException {
		return this.getManutencaoPrescricaoEnfermagemON()
				.buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO(prescricaoEnfermagemId);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#obterPrescricaoEnfermagemPorId(br.gov.mec.aghu.model.EpePrescricaoEnfermagemId)
	 */
	@Override
	public EpePrescricaoEnfermagem obterPrescricaoEnfermagemPorId(EpePrescricaoEnfermagemId prescricaoEnfermagemId){
		return this.getEpePrescricaoEnfermagemDAO().obterPorChavePrimaria(prescricaoEnfermagemId);
	}
	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#popularDadosCabecalhoPrescricaoEnfermagemVO(br.gov.mec.aghu.model.EpePrescricaoEnfermagem)
	 */
	@Override
	public PrescricaoEnfermagemVO popularDadosCabecalhoPrescricaoEnfermagemVO(
			EpePrescricaoEnfermagem prescricaoEnfermagem) throws ApplicationBusinessException {
		return this.getManutencaoPrescricaoEnfermagemON()
			.popularDadosCabecalhoPrescricaoEnfermagemVO(prescricaoEnfermagem);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#buscarCuidadosPrescricaoEnfermagem(br.gov.mec.aghu.model.EpePrescricaoEnfermagemId, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<CuidadoVO> buscarCuidadosPrescricaoEnfermagem(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId, Boolean listarTodas) {
		return this.getManutencaoPrescricaoEnfermagemON()
			.buscarCuidadosPrescricaoEnfermagem(prescricaoEnfermagemId, listarTodas);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#removerCuidadosSelecionados(java.util.List)
	 */
	@Override
	public void removerCuidadosSelecionados(List<CuidadoVO> listaCuidadoVO) throws ApplicationBusinessException {
		this.getManutencaoPrescricaoCuidadoON().removerCuidadosSelecionados(listaCuidadoVO);
	}	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(java.lang.Short, java.lang.Short, java.lang.String)
	 */
	@Override
	public List<SinalSintomaVO> pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String descricao) {
		return this.getSinalSintomaON().pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(dgnSnbGnbSeq, dgnSnbSequencia, descricao);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarDiagnosticoPorSinalSintoma(java.lang.Integer)
	 */
	@Override
	public List<DiagnosticoVO> pesquisarDiagnosticoPorSinalSintoma(Integer codigo) {
		return this.getSinalSintomaON().pesquisarDiagnosticoPorSinalSintoma(codigo);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarDiagnosticos(java.lang.Short, java.lang.Short, java.lang.String, java.lang.Short)
	 */
	@Override
	public List<DiagnosticoVO> pesquisarDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String dgnDescricao, Short dgnSequencia){
		return this.getDiagnosticoON().pesquisarDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnDescricao, dgnSequencia);
	}
	
	@Override
	public List<EpeDiagnostico> pesquisarDiagnosticos(Short gnbSeq, Short snbSequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return this.getEpeDiagnosticoDAO().pesquisarDiagnosticos(gnbSeq, snbSequencia, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarDiagnosticosCount(Short gnbSeq, Short snbSequencia){
		return this.getEpeDiagnosticoDAO().pesquisarDiagnosticosCount(gnbSeq, snbSequencia);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarEtiologiaPorDiagnostico(java.lang.Short, java.lang.Short, java.lang.Short)
	 */
	@Override
	public List<EtiologiaVO> pesquisarEtiologiaPorDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return this.getSinalSintomaON().pesquisarEtiologiaPorDiagnostico(snbGnbSeq, snbSequencia, sequencia);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarCuidadosAtivosAtribuidos(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<EpePrescricoesCuidados> pesquisarCuidadosAtivosAtribuidos(Integer penSeq, Integer penAtdSeq, Date dthrFim) {
		return this.getEpePrescricoesCuidadosDAO().pesquisarCuidadosAtivosAtribuidos(penSeq, penAtdSeq, dthrFim);
	}
	
	@Override
	public List<CuidadoVO> pesquisarCuidadosPrescEnfermagem(Integer penSeq, Integer penAtdSeq, Date dthrFim) {
		return getManutencaoPrescricaoCuidadoON().pesquisarCuidadosPrescEnfermagem(penSeq, penAtdSeq, dthrFim);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarGruposNecesBasica(java.lang.Object)
	 */
	@Override
	public List<EpeGrupoNecesBasica> pesquisarGruposNecesBasica(Object filtro){
		return this.getEpeGrupoNecesBasicaDAO().pesquisarGrupoNecesBasicaAtivo((String)filtro);
	}
	
	@Override
	public List<EpeGrupoNecesBasica> pesquisarGrupoNecesBasicaAtivoOrderSeq(Object filtro){
		return this.getEpeGrupoNecesBasicaDAO().pesquisarGrupoNecesBasicaAtivoOrderSeq((String)filtro);
	}
	
	@Override
	public Long pesquisarGrupoNecesBasicaAtivoOrderSeqCount(Object filtro){
		return this.getEpeGrupoNecesBasicaDAO().pesquisarGrupoNecesBasicaAtivoOrderSeqCount((String)filtro);
	}
	
	@Override
	public EpeGrupoNecesBasica obterEpeGrupoNecesBasica(Short seq){
		return this.getEpeGrupoNecesBasicaDAO().obterPorChavePrimaria(seq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarSubgruposNecesBasica(java.lang.Object, java.lang.Short)
	 */
	@Override
	public List<EpeSubgrupoNecesBasica> pesquisarSubgruposNecesBasica(Object filtro, Short gnbSeq){
		return this.getEpeSubgrupoNecesBasicaDAO().pesquisarSubgrupoNecessBasicaAtivo((String)filtro, gnbSeq);
	}
	
	@Override
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaAtivoOrderSeq(Object filtro, Short gnbSeq){
		return this.getEpeSubgrupoNecesBasicaDAO().pesquisarSubgrupoNecessBasicaAtivoOrderSeq((String)filtro, gnbSeq);
	}
	
	@Override
	public Long pesquisarSubgrupoNecessBasicaAtivoOrderSeqCount(Object filtro, Short gnbSeq){
		return this.getEpeSubgrupoNecesBasicaDAO().pesquisarSubgrupoNecessBasicaAtivoOrderSeqCount((String)filtro, gnbSeq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarDiagnosticos(java.lang.Object, java.lang.Short, java.lang.Short)
	 */
	@Override
	public List<EpeDiagnostico> pesquisarDiagnosticos(Object filtro, Short gnbSeq, Short snbSequencia){
		return this.getEpeDiagnosticoDAO().pesquisarDiagnosticos((String)filtro, gnbSeq, snbSequencia);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarEtiologias(java.lang.Object, java.lang.Short, java.lang.Short, java.lang.Short)
	 */
	@Override
	public List<EpeFatRelacionado> pesquisarEtiologias(Object filtro, Short gnbSeq, Short snbSequencia, Short sequencia){
		return this.getEpeFatRelacionadoDAO().pesquisarEtiologias((String)filtro, gnbSeq, snbSequencia, sequencia);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#gerarAprazamentoPrescricaoEnfermagem(br.gov.mec.aghu.model.EpePrescricaoEnfermagem, java.util.Date, java.util.Date, br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento, br.gov.mec.aghu.prescricaomedica.constantes.TipoItemAprazamento, java.util.Date, java.lang.Boolean, java.lang.Short)
	 */
	@Override
	public List<String> gerarAprazamentoPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem,
			Date dthrInicioItem, Date dthrFimItem,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			TipoItemAprazamento tipoItem, Date dtHrInicioTratamento,
			Boolean indNecessario, Short frequencia) {
		
		return this.getAprazamentoPrescricaoEnfermagemRN().gerarAprazamentoPrescricaoEnfermagem(prescricaoEnfermagem,
				dthrInicioItem, dthrFimItem, tipoFrequenciaAprazamento,
				tipoItem, dtHrInicioTratamento, indNecessario, frequencia);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#obterRelatorioPrescricaoEnfermagem(br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO)
	 */
	@Override
	public List<PrescricaoEnfermagemVO> obterRelatorioPrescricaoEnfermagem(PrescricaoEnfermagemVO prescricaoEnfermagemVO) throws BaseException {
		return getManutencaoPrescricaoEnfermagemON().popularRelatorioPrescricaoEnfermagem(prescricaoEnfermagemVO);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#verificaListaCuidadosVO(br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO)
	 */
	@Override
	public void verificaListaCuidadosVO(PrescricaoEnfermagemVO prescricaoEnfermagemVO) throws BaseException {
		this.getManutencaoPrescricaoEnfermagemON().verificaListaCuidadosVO(prescricaoEnfermagemVO);
	}
	

	@Override
	public void cancelaPrescricaoEnfermagem(Integer penSeqAtendimento, Integer penSeq) throws ApplicationBusinessException {
		this.getManutencaoPrescricaoCuidadoRN().cancelaPrescricaoEnfermagem(penSeqAtendimento, penSeq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarCuidadosAtivosPorGrupoDiagnosticoEtiologiaUnica(java.lang.Short, java.lang.Short, java.lang.Short, java.lang.Short)
	 */
	@Override
	public List<EpeCuidados> pesquisarCuidadosAtivosPorGrupoDiagnosticoEtiologiaUnica(Short fdgDgnSnbGnbSeq, 
			Short fdgDgnSnbSequencia, Short fdgDgnSequencia, Short fdgFreSeq) {
		return this.getEpeCuidadosDAO().pesquisarCuidadosAtivosPorGrupoDiagnosticoEtiologiaUnica(fdgDgnSnbGnbSeq, fdgDgnSnbSequencia, fdgDgnSequencia, fdgFreSeq);
	}
	
	@Override
	public List<EpeCuidados> listarCuidadosEnfermagem(Short codigo, String descricao, Boolean indCci,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getEpeCuidadosDAO().listarCuidadosEnfermagem(codigo, descricao, indCci, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarCuidadosEnfermagemCount(Short codigo, String descricao, Boolean indCci) {
		return getEpeCuidadosDAO().listarCuidadosEnfermagemCount(codigo, descricao, indCci);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCuidadosInteresseCCIH','alterar')}")
	public void atualizarEpeCuidados(EpeCuidados epeCuidados) {
		this.epeCuidadosRN.atualizarEpeCuidados(epeCuidados);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#obterDescricaoEtiologiaPorSeq(java.lang.Short)
	 */
	@Override
	public EpeFatRelacionado obterDescricaoEtiologiaPorSeq(Short fdgFreSqe) {
		return this.getEpeFatRelacionadoDAO().obterPorChavePrimaria(fdgFreSqe);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#listarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(java.lang.Integer, java.util.Date, java.util.Date, java.util.Date)
	 */
	@Override
	public List<DiagnosticoEtiologiaVO> listarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(Integer atdSeq, 
			Date dthrInicio, Date dthrFim, Date dthrMovimento) {
		return getEpePrescCuidDiagnosticoDAO()
				.listarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(atdSeq, dthrInicio, dthrFim, dthrMovimento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#removerPrescCuidadosDiagnosticosSelecionados(java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void removerPrescCuidadosDiagnosticosSelecionados(List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO, 
			Integer penAtdSeq, Integer penSeq) throws ApplicationBusinessException {
		getEncerramentoDiagnosticoON().removerPrescCuidadosDiagnosticosSelecionados(
				listaDiagnosticoEtiologiaVO, penAtdSeq, penSeq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#buscarItensPrescricaoEnfermagem(br.gov.mec.aghu.model.EpePrescricaoEnfermagemId, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<ItemPrescricaoEnfermagemVO> buscarItensPrescricaoEnfermagem(
			EpePrescricaoEnfermagemId prescricaoId, Boolean listarTodos)
			throws ApplicationBusinessException {
		return this.getSelecaoPrescricaoEnfermagemON().buscarItensPrescricaoEnfermagem(
				prescricaoId, listarTodos);
	}
	
	@Override
	@BypassInactiveModule
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemPorAtendimentoEDataFimAteDataAtual(
			final Integer atdSeq, final Date dataFim) {
		return getEpePrescricaoEnfermagemDAO().pesquisarPrescricaoEnfermagemPorAtendimentoEDataFimAteDataAtual(atdSeq, dataFim);
	}
	
	@Override
	public EpeDiagnostico obterDiagnosticoPorChavePrimaria(EpeDiagnosticoId id) {
		return getEpeDiagnosticoDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public EpeSubgrupoNecesBasica obterEpeSubgrupoNecesBasicaPorChavePrimaria(EpeSubgrupoNecesBasicaId id) {
		return getEpeSubgrupoNecesBasicaDAO().obterPorChavePrimaria(id);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#agendarGerarDadosSumarioPrescricaoEnfermagem(java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void agendarGerarDadosSumarioPrescricaoEnfermagem(String cron, Date dataInicio, Date dataFim) throws ApplicationBusinessException {
		getManterSumarioSchedulerRN().gerarDadosSumarioPrescricaoEnfermagem(cron, dataInicio, dataFim);
	}
	

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#geraDadosSumarioPrescricaoEnfermagem(java.lang.Integer, br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value=3, unit=TimeUnit.HOURS)
	public void geraDadosSumarioPrescricaoEnfermagem(Integer seqAtendimento, DominioTipoEmissaoSumario tipoEmissao) throws ApplicationBusinessException {
		this.getManterSumarioEnfermagemRN().geraDadosSumarioPrescricaoEnfermagem(seqAtendimento, tipoEmissao);
	}
	

	
	protected SelecaoPrescricaoEnfermagemON getSelecaoPrescricaoEnfermagemON(){
		return selecaoPrescricaoEnfermagemON;
	}
	
	protected ManterSumarioSchedulerRN getManterSumarioSchedulerRN(){
		return manterSumarioSchedulerRN;
	}
	
	
	private ListaPacientesEnfermagemON getListaPacientesEnfermagemON() {
		return listaPacientesEnfermagemON;
	}

	private ElaboracaoPrescricaoEnfermagemON getElaboracaoPrescricaoEnfermagemON() {
		return elaboracaoPrescricaoEnfermagemON;
	}
	
	private ManutencaoPrescricaoEnfermagemON getManutencaoPrescricaoEnfermagemON() {
		return manutencaoPrescricaoEnfermagemON;
	}
	
	private ManutencaoPrescricaoCuidadoON getManutencaoPrescricaoCuidadoON() {
		return manutencaoPrescricaoCuidadoON;
	}
	
	private SinalSintomaON getSinalSintomaON() {
		return sinalSintomaON;
	}
	
	private DiagnosticoON getDiagnosticoON() {
		return diagnosticoON;
	}
	
	private ManterSumarioEnfermagemRN getManterSumarioEnfermagemRN() {
		return manterSumarioEnfermagemRN;
	}
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO() {
		return epePrescricaoEnfermagemDAO;
	}
	
	protected EpeCuidadosDAO getEpeCuidadosDAO() {
		return epeCuidadosDAO;
	}
	
	protected EpeGrupoNecesBasicaDAO getEpeGrupoNecesBasicaDAO() {
		return epeGrupoNecesBasicaDAO;
	}
	
	protected EpeSubgrupoNecesBasicaDAO getEpeSubgrupoNecesBasicaDAO() {
		return epeSubgrupoNecesBasicaDAO;
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO(){
		return epePrescricoesCuidadosDAO;
	}
	
	protected EpeDiagnosticoDAO getEpeDiagnosticoDAO() {
		return epeDiagnosticoDAO;
	}
	
	protected EpeFatRelDiagnosticoDAO getEpeFatRelDiagnosticoDAO() {
		return epeFatRelDiagnosticoDAO;
	}
	
	protected EpeFatRelacionadoDAO getEpeFatRelacionadoDAO() {
		return epeFatRelacionadoDAO;
	}
	
	private AprazamentoPrescricaoEnfermagemRN getAprazamentoPrescricaoEnfermagemRN() {
		return aprazamentoPrescricaoEnfermagemRN;
	}
	
	private ManutencaoPrescricaoCuidadoRN getManutencaoPrescricaoCuidadoRN() {
		return manutencaoPrescricaoCuidadoRN;
	}
	
	protected FinalizacaoPrescricaoEnfermagemRN getFinalizacaoPrescricaoEnfermagemRN(){
		return finalizacaoPrescricaoEnfermagemRN;
	}

	protected FinalizacaoPendentePrescricaoEnfermagemRN getFinalizacaoPendentePrescricaoEnfermagemRN(){
		return finalizacaoPendentePrescricaoEnfermagemRN;
	}
	
	protected ElaboracaoPrescricaoEnfermagemRN getElaboracaoPrescricaoEnfermagemRN(){
		return elaboracaoPrescricaoEnfermagemRN;
	}
	
	protected EpePrescCuidDiagnosticoDAO getEpePrescCuidDiagnosticoDAO() {
		return epePrescCuidDiagnosticoDAO;
	}
	
	protected EncerramentoDiagnosticoON getEncerramentoDiagnosticoON() {
		return encerramentoDiagnosticoON;
	}
	
	protected ManterCuidadosON getManterCuidadosON() {
		return manterCuidadosON;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#pesquisarCuidadosEnfermagem(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public List<EpeCuidados> pesquisarCuidadosEnfermagem(String parametro) {
		return this.getEpeCuidadosDAO().pesquisarCuidadosAtivosPorSeqOuDescricao(
				parametro);
	}

	protected EpeNotificacaoDAO getEpeNotificacaoDAO() {
		return epeNotificacaoDAO;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaoenfermagem.business.IPrescricaoEnfermagemFacade#existeComunicadoUlceraPressaoPorAtendimento(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public boolean existeComunicadoUlceraPressaoPorAtendimento(Integer atdSeq) {
		return this.getEpeNotificacaoDAO()
				.existeComunicadoUlceraPressaoPorAtendimento(atdSeq);
	}
	
	@BypassInactiveModule
	public List<RelSumarioPrescricaoEnfermagemVO> pesquisaGrupoDescricaoStatus(
			SumarioPrescricaoEnfermagemVO prescricao, boolean limitValor){
		return getEpeDataItemSumarioDAO().pesquisaGrupoDescricaoStatus(prescricao, limitValor);
	}
	
	protected EpeDataItemSumarioDAO getEpeDataItemSumarioDAO() {
		return epeDataItemSumarioDAO;
	}
	
	
	@BypassInactiveModule
	public List<EpeDataItemSumario> listarDataItemSumario(Integer apaAtdSeq){
		return getEpeDataItemSumarioDAO().listarDataItemSumario(apaAtdSeq);
	}

	@BypassInactiveModule
	public List<EpePrescricaoEnfermagem> listarPrescricoesParaGerarSumarioDePrescricaoEnfermagem(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao){
		return getEpePrescricaoEnfermagemDAO().listarPrescricoesParaGerarSumarioDePrescricaoEnfermagem(seqAtendimento, dthrInicioPrescricao, dthrFimPrescricao);
	}
	
	public List<Date> executarCursorEpe(Integer atdSeq) {
		return getEpePrescricaoEnfermagemDAO().executarCursorEpe(atdSeq);
	}
	
	protected EpeUnidPacAtendimentoDAO getEpeUnidPacAtendimentoDAO() {
		return epeUnidPacAtendimentoDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<EpeUnidPacAtendimento> obterEpeUnidPacAtendimentoPorApaAtdSeqApaSeq(Integer apaAtdSeq, Integer apaSeq) {
		return getEpeUnidPacAtendimentoDAO().obterEpeUnidPacAtendimentoPorApaAtdSeqApaSeq(apaAtdSeq, apaSeq);
	}
	
	@Override
	public EpeGrupoNecesBasica obterDescricaoGrupoNecessidadesHumanasPorSeq(Short seq) {
		return this.getEpeGrupoNecesBasicaDAO().obterPorChavePrimaria(seq);
	}
	
	protected EpeCuidadoUnfDAO getEpeCuidadoUnfDAO() {
		return epeCuidadoUnfDAO;
	}
	
	public List<EpeCuidadoUnf> pesquisarEpeCuidadoUnf(Short seq) {
		return getEpeCuidadoUnfDAO().pesquisarEpeCuidadoUnf(seq);
	}

	@Override		  
	public void validarCuidadoRotina(Boolean indRotina, List<EpeCuidadoUnf> listaCuidadoUnidades) throws ApplicationBusinessException {
		getManterCuidadosON().validarCuidadoRotina(indRotina, listaCuidadoUnidades);
	}

	public List<EpeCuidados> pesquisarEpeCuidadosPorCodigoDescricao(Short seq, String descricao, int firstResult, int maxResults, 
			String orderProperty, Boolean asc, Boolean orderBy){
		return getEpeCuidadosDAO().pesquisarEpeCuidadosPorCodigoDescricao(seq, descricao, firstResult, maxResults, orderProperty, asc, orderBy);
	}

	public Long pesquisarEpeCuidadosPorCodigoDescricaoCount(Short seq, String descricao) {
		return getEpeCuidadosDAO().pesquisarEpeCuidadosPorCodigoDescricaoCount(seq, descricao);
	}

	@Override
	public String alterarSituacao(EpeCuidadoUnf epeCuidadoUnf) {
		return getManterCuidadosON().alterarSituacao(epeCuidadoUnf);
	}

	@Override
	public EpeCuidados obterEpeCuidadosPorSeq(Short seq){
		Enum[] fetchArgsLeftJoin = {EpeCuidados.Fields.TIPO_FREQUENCIA_APRAZAMENTO};
		return epeCuidadosDAO.obterPorChavePrimaria(seq, null, fetchArgsLeftJoin);
	}
	

	@Override
	public List<EpeCuidadoUnf> obterEpeCuidadoUnfPorEpeCuidadoSeq(Short seqEpeCuidado) {
		return getEpeCuidadoUnfDAO().obterEpeCuidadoUnfPorEpeCuidadoSeq(seqEpeCuidado);
	}

	@Override
	public void restaurarEpeCuidadoUnf(List<EpeCuidadoUnf> listaCuidadoUnidades) {
		getManterCuidadosON().restaurarEpeCuidadoUnf(listaCuidadoUnidades);
	}

	@Override
	public String gravarEpeCuidadoUnfs(EpeCuidadoUnf epeCuidadoUnf, EpeCuidados epeCuidado, AghUnidadesFuncionais aghUnidadeFuncional) throws ApplicationBusinessException {
		return getManterCuidadosON().gravarEpeCuidadoUnfs(epeCuidadoUnf, epeCuidado, aghUnidadeFuncional);
	}

	@Override
	public void removerEpeCuidadoUnf(EpeCuidadoUnfId id)  throws ApplicationBusinessException{
		manterCuidadosON.removerEpeCuidadoUnf(id);
	}
		
	public List<EpeGrupoNecesBasica> pesquisarGruposNecesBasicaTodos(Object filtro) {
		return this.getEpeGrupoNecesBasicaDAO().pesquisarGrupoNecesBasica((String) filtro);
	}

	public Long pesquisarGruposNecesBasicaTodosCount(Object filtro) {
		return this.getEpeGrupoNecesBasicaDAO().pesquisarGrupoNecesBasicaCount((String) filtro);
	}

	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaTodos(Object filtro, Short gnbSeq) {
		return this.getEpeSubgrupoNecesBasicaDAO().pesquisarSubgrupoNecessBasica((String) filtro, gnbSeq);
	}

	public Long pesquisarSubgrupoNecessBasicaTodosCount(Object filtro, Short gnbSeq) {
		return this.getEpeSubgrupoNecesBasicaDAO().pesquisarSubgrupoNecessBasicaCount((String) filtro, gnbSeq);
	}

	public List<EpeDiagnostico> pesquisarDiagnosticosTodos(Object filtro, Short gnbSeq, Short snbSequencia) {
		return this.getEpeDiagnosticoDAO().pesquisarDiagnosticosTodos((String) filtro, gnbSeq, snbSequencia);
	}

	public Long pesquisarDiagnosticosTodosCount(Object filtro, Short gnbSeq, Short snbSequencia) {
		return this.getEpeDiagnosticoDAO().pesquisarDiagnosticosTodosCount((String) filtro, gnbSeq, snbSequencia);
	}

	public List<DiagnosticoCuidadoVO> pesquisarDiagnosticosLista(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, Short freSeq, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.getEpeFatRelDiagnosticoDAO().pesquisarDiagnosticos(snbGnbSeq, snbSequencia, dgnSequencia, freSeq, firstResult, maxResult, orderProperty,
				asc);
	}

	public Long pesquisarDiagnosticosListaCount(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, Short freSeq) {
		return this.getEpeFatRelDiagnosticoDAO().pesquisarDiagnosticosCount(snbGnbSeq, snbSequencia, dgnSequencia, freSeq);
	}
	
	public List<EpeCuidados> pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticos(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia,
			Short dgnSequencia, Short freSeq) {
		return this.getEpeCuidadosDAO().pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticos(parametro, dgnSnbGnbSeq, dgnSnbSequencia,
				dgnSequencia, freSeq);
	}

	public Long pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticosCount(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia,
			Short dgnSequencia, Short freSeq) {
		return this.getEpeCuidadosDAO().pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticosCount(parametro, dgnSnbGnbSeq, dgnSnbSequencia,
				dgnSequencia, freSeq); 
	}
	
	protected EpeCuidadoDiagnosticoDAO getEpeCuidadoDiagnosticoDAO() {
		return epeCuidadoDiagnosticoDAO;
	}
	
	public List<EpeCuidadoDiagnostico> obterEpeCuidadoDiagnosticoPorEpeCuidadoSeq(Short cuidadoSeq) {
		return getEpeCuidadoDiagnosticoDAO().obterEpeCuidadoDiagnosticoPorEpeCuidadoSeq(cuidadoSeq);
	}
	
	public List<EpeCuidadoDiagnostico> pesquisarCuidadosDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getEpeCuidadoDiagnosticoDAO().pesquisarCuidadosDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq, firstResult, maxResult,
				orderProperty, asc);
	}

	public Long pesquisarCuidadosDiagnosticosCount(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		return getEpeCuidadoDiagnosticoDAO().pesquisarCuidadosDiagnosticosCount(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq);
	}
	
	@Override
	public List<EpeCuidados> pesquisarEpeCuidadosPorCodigo(Short seq) {
		return getRelatorioRotinaCuidadoON().pesquisarEpeCuidadosPorCodigo(seq);
	}

	private RelatorioRotinaCuidadoON getRelatorioRotinaCuidadoON() {
		return relatorioRotinaCuidadoON;
	}

	@Override
	public String gravar(EpeCuidados epeCuidado, Short seqEpeCuidado) throws ApplicationBusinessException {
		return getManterCuidadosON().gravar(epeCuidado, seqEpeCuidado);
		
	}
	
	public List<EpeCuidados> pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentos(String parametro, Integer medMatCodigo) {
		return this.getEpeCuidadosDAO().pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentos(parametro, medMatCodigo);
	}

	public Long pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentosCount(String parametro, Integer medMatCodigo) {
		return this.getEpeCuidadosDAO().pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentosCount(parametro, medMatCodigo);
	}

	public List<CuidadoMedicamentoVO> pesquisarCuidadosMedicamentos(Integer matCodigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getEpeCuidadoMedicamentoDAO().pesquisarCuidadosMedicamentos(matCodigo, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCuidadosMedicamentosCount(Integer matCodigo) {
		return getEpeCuidadoMedicamentoDAO().pesquisarCuidadosMedicamentosCount(matCodigo);
	}

	protected EpeCuidadoMedicamentoDAO getEpeCuidadoMedicamentoDAO() {
		return epeCuidadoMedicamentoDAO;
	}

	@Override
	public String excluirEpeCuidados(Short seq) throws ApplicationBusinessException, BaseListException,
			ApplicationBusinessException {
		return manterCuidadosON.excluirEpeCuidados(seq);
	}
	
	@Override
	public List<EpeDiagnostico> pesquisarDiagnosticosPorGrpSgrpDiag(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getEpeDiagnosticoDAO().pesquisarDiagnosticosPorGrpSgrpDiag(snbGnbSeq, snbSequencia, dgnSequencia, 
				firstResult, maxResult,	orderProperty, asc);
	}

	@Override
	public Long pesquisarDiagnosticosPorGrpSgrpDiagCount(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia) {
		return getEpeDiagnosticoDAO().pesquisarDiagnosticosPorGrpSgrpDiagCount(snbGnbSeq, snbSequencia, dgnSequencia);
	}

	@Override
	public Long pesquisarEtiologiasDiagnosticosCount(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return getEpeFatRelDiagnosticoDAO().pesquisarEtiologiasDiagnosticosCount(snbGnbSeq, snbSequencia, sequencia);
	}

	@Override
	public List<EpeFatRelDiagnostico> pesquisarEtiologiasDiagnosticos(Short snbGnbSeq, Short snbSequencia, Short sequencia,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return epeFatRelDiagnosticoDAO.pesquisarEtiologiasDiagnosticos(snbGnbSeq, snbSequencia, sequencia, 
				firstResult, maxResult,	orderProperty, asc);
	}
	
	@Override
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemPorAtendimento(
			Integer atdSeq){
		return epePrescricaoEnfermagemDAO.pesquisarPrescricaoEnfermagemPorAtendimento(atdSeq);
	}
	
	@Override
	public List<EpePrescricaoEnfermagemVO> pesquisarPrescricaoEnfermagemPorAtendimentoEmergencia(Integer atdSeq) {
		return diagnosticoON.pesquisarPrescricaoEnfermagemPorAtendimento(atdSeq);
	}
	
	@Override
	public List<EpeCuidados> pesquisarCuidadosdeRotinasAtivos(Short unidadeF) {
		return this.getEpeCuidadosDAO().pesquisarCuidadosdeRotinasAtivos(unidadeF, 
														this.getEpeCuidadoUnfDAO().pesquisarTodos(),
														this.getEpeCuidadoUnfDAO().pesquisarEpeCuidadoUnfPorUnf(unidadeF));
	}
	
	
	@Override
	public List<ListaPacientePrescricaoVO> retornarListaImpressaoPrescricaoEnfermagem(List<PacienteEnfermagemVO> listaPacientesEnfermagem){
		return this.getListaPacientesEnfermagemON().retornarListaImpressaoPrescricaoEnfermagem(listaPacientesEnfermagem);
	}
	
	@Override
	public void validarDataPrescricao(Date dtPrescricao, List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem) throws ApplicationBusinessException {
		getElaboracaoPrescricaoEnfermagemON().validarDataPrescricao(dtPrescricao, listaPrescricaoEnfermagem);
	}
	
	@Override
	public AghAtendimentos obterUltimoAtendimentoEmAndamentoPorPaciente(
			Integer pacCodigo, String nome) throws ApplicationBusinessException {
		return getVisualizarAnamneseEvolucoesRN().obterUltimoAtendimentoEmAndamentoPorPaciente(pacCodigo, nome);
	}
	
	private VisualizarAnamneseEvolucoesRN getVisualizarAnamneseEvolucoesRN(){
		return visualizarAnamneseEvolucoesRN;
	}

}

