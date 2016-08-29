/**
 * Fachada para Perinatologia
 */
package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioOcorrenciaIntercorrenciaGestacao;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.model.McoAchadoExameFisicos;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsId;
import br.gov.mec.aghu.model.McoAnamneseEfsJn;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoEscalaLeitoRns;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoGestacaoPacientes;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIddGestBallards;
import br.gov.mec.aghu.model.McoIddGestCapurros;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoIntercorPasatus;
import br.gov.mec.aghu.model.McoIntercorrencia;
import br.gov.mec.aghu.model.McoIntercorrenciaGestacoes;
import br.gov.mec.aghu.model.McoIntercorrenciaNascs;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos.Fields;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNascimentosId;
import br.gov.mec.aghu.model.McoNotaAdicional;
import br.gov.mec.aghu.model.McoPartos;
import br.gov.mec.aghu.model.McoPlacar;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.model.McoProcReanimacao;
import br.gov.mec.aghu.model.McoProcedimentoObstetrico;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoReanimacaoRns;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoRecemNascidosId;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.paciente.prontuario.vo.InformacoesPerinataisVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;
import br.gov.mec.aghu.perinatologia.dao.McoAchadoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAchadoExameFisicosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAnamneseEfsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAnamneseEfsJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoApgarsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAtendTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoBolsaRotasDAO;
import br.gov.mec.aghu.perinatologia.dao.McoCesarianasDAO;
import br.gov.mec.aghu.perinatologia.dao.McoCondutaDAO;
import br.gov.mec.aghu.perinatologia.dao.McoEscalaLeitoRnsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoExameFisicoRnsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoForcipesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacaoPacientesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIddGestBallardsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIddGestCapurrosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIndicacaoNascimentoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorPasatusDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorrenciaDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorrenciaGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorrenciaNascsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoMedicamentoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascIndicacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNotaAdicionalDAO;
import br.gov.mec.aghu.perinatologia.dao.McoPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoPlanoIniciaisDAO;
import br.gov.mec.aghu.perinatologia.dao.McoProcReanimacaoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoProcedimentoObstetricoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoProfNascsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoReanimacaoRnsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoResultadoExameSignifsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSindromeDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSnappesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.VMcoExamesDAO;
import br.gov.mec.aghu.perinatologia.vo.RelatorioSnappeVO;
import br.gov.mec.aghu.view.VMcoExames;

/**
 * @author marcelofilho
 *
 */

@Modulo(ModuloEnum.PERINATOLOGIA)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class PerinatologiaFacade extends BaseFacade implements IPerinatologiaFacade {

	
	@EJB
	private PerinatologiaON perinatologiaON;
	
	@Inject
	private McoProcedimentoObstetricoDAO mcoProcedimentoObstetricoDAO;
	
	@Inject
	private McoAnamneseEfsJnDAO mcoAnamneseEfsJnDAO;
	
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@Inject
	private McoAtendTrabPartosDAO mcoAtendTrabPartosDAO;
	
	@Inject
	private McoIddGestBallardsDAO mcoIddGestBallardsDAO;
	
	@Inject
	private VMcoExamesDAO vMcoExamesDAO;
	
	@Inject
	private McoBolsaRotasDAO mcoBolsaRotasDAO;
	
	@Inject
	private McoIddGestCapurrosDAO mcoIddGestCapurrosDAO;
	
	@Inject
	private McoApgarsDAO mcoApgarsDAO;
	
	@Inject
	private McoExameFisicoRnsDAO mcoExameFisicoRnsDAO;
	
	@Inject
	private McoIntercorPasatusDAO mcoIntercorPasatusDAO;
	
	@Inject
	private McoResultadoExameSignifsDAO mcoResultadoExameSignifsDAO;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;
	
	@Inject
	private McoNotaAdicionalDAO mcoNotaAdicionalDAO;
	
	@Inject
	private McoPlanoIniciaisDAO mcoPlanoIniciaisDAO;
	
	@Inject
	private McoIntercorrenciaDAO mcoIntercorrenciaDAO;
	
	@Inject
	private McoProfNascsDAO mcoProfNascsDAO;
	
	@Inject
	private McoForcipesDAO mcoForcipesDAO;
	
	@Inject
	private McoTrabPartosDAO mcoTrabPartosDAO;
	
	@Inject
	private McoMedicamentoTrabPartosDAO mcoMedicamentoTrabPartosDAO;
	
	@Inject
	private McoEscalaLeitoRnsDAO mcoEscalaLeitoRnsDAO;
	
	@Inject
	private McoGestacaoPacientesDAO mcoGestacaoPacientesDAO;
	
	@Inject
	private McoReanimacaoRnsDAO mcoReanimacaoRnsDAO;
	
	@Inject
	private McoNascIndicacoesDAO mcoNascIndicacoesDAO;
	
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@Inject
	private McoAnamneseEfsDAO mcoAnamneseEfsDAO;
	
	@Inject
	private McoIndicacaoNascimentoDAO mcoIndicacaoNascimentoDAO;
	
	@Inject
	private McoIntercorrenciaNascsDAO mcoIntercorrenciaNascsDAO;
	
	@Inject
	private McoAchadoExameFisicosDAO mcoAchadoExameFisicosDAO;
	
	@Inject
	private McoIntercorrenciaGestacoesDAO mcoIntercorrenciaGestacoesDAO;
	
	@Inject
	private McoCondutaDAO mcoCondutaDAO;
	
	@Inject
	private McoSindromeDAO mcoSindromeDAO;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoProcReanimacaoDAO mcoProcReanimacaoDAO;
	
	@Inject
	private McoSnappesDAO mcoSnappesDAO;
	
	@Inject
	private McoPartosDAO mcoPartosDAO;
	
	@Inject
	private McoAchadoDAO mcoAchadoDAO;
	
	@Inject
	private McoCesarianasDAO mcoCesarianasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7482551853487001399L;

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#persistirPlacar(br.gov.mec.aghu.model.McoPlacar)
	 */
	@Override
	public void persistirPlacar(final McoPlacar placar) {
		this.getPerinatologiaON().persistirPlacar(placar);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#buscarPlacar(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public McoPlacar buscarPlacar(final Integer codigoPaciente) {
		return this.getPerinatologiaON().buscarPlacar(codigoPaciente);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#listaRecemNascidosPorCodigoPaciente(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public List<McoRecemNascidos> listaRecemNascidosPorCodigoPaciente(final Integer codigoPaciente) {
		return this.getMcoRecemNascidosDAO().listaRecemNascidosPorCodigoPaciente(codigoPaciente);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#obterMcoGestacoes(br.gov.mec.aghu.model.McoGestacoesId)
	 */
	@Override
	@BypassInactiveModule
	public McoGestacoes obterMcoGestacoes(final McoGestacoesId id) {
		return this.getMcoGestacoesDAO().obterPorChavePrimaria(id);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#obterRecemNascidoGestacoesPaciente(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public McoRecemNascidos obterRecemNascidoGestacoesPaciente(final Integer pPAcCodigo, final Short pGsoSeqp, final Integer pSeqp) {
		return this.getMcoRecemNascidosDAO().obterRecemNascidoGestacoesPaciente(pPAcCodigo, pGsoSeqp, pSeqp);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#inserirMcoRecemNascidos(br.gov.mec.aghu.model.McoRecemNascidos, boolean)
	 */
	@Override
	public void inserirMcoRecemNascidos(final McoRecemNascidos mcoRecemNascidos, final boolean flush) {
		// TODO Implementar uma ON/RN com as regras de negócio de inserção
		McoRecemNascidosDAO mcoRecemNascidosDAO = this.getMcoRecemNascidosDAO();
		mcoRecemNascidosDAO.persistir(mcoRecemNascidos);
		if (flush) {
			mcoRecemNascidosDAO.flush();
		}
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#atualizarMcoRecemNascidos(br.gov.mec.aghu.model.McoRecemNascidos, boolean)
	 */
	@Override
	public void atualizarMcoRecemNascidos(final McoRecemNascidos mcoRecemNascidos, final boolean flush) {
		// TODO Implementar uma ON/RN com as regras de negócio de atualização
		McoRecemNascidosDAO mcoRecemNascidosDAO = this.getMcoRecemNascidosDAO();
		mcoRecemNascidosDAO.atualizar(mcoRecemNascidos);
		if (flush) {
			mcoRecemNascidosDAO.flush();
		}
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#obterMcoNascimento(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public McoNascimentos obterMcoNascimento(final Integer seqp, final Integer codigoPaciente, final Short sequence) {
		return this.getMcoNascimentosDAO().obterMcoNascimento(seqp, codigoPaciente, sequence);
	}

	@Override
	public List<McoSnappes> listarSnappesPorCodigoPaciente(Integer pacCodigo) {
		return this.getMcoSnappesDAO()
				.listarSnappesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<McoIddGestBallards> listarIddGestBallardsPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getMcoIddGestBallardsDAO()
				.listarIddGestBallardsPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<McoIddGestCapurros> listarIddGestCapurrosPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getMcoIddGestCapurrosDAO()
				.listarIddGestCapurrosPorCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<McoGestacaoPacientes> listarGestacoesPacientePorCodigoPaciente(
			Integer pacCodigo) {
		return this.getMcoGestacaoPacientesDAO()
				.listarGestacoesPacientePorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<McoAnamneseEfs> listarAnamnesesEfsPorPacCodigo(Integer pacCodigo) {
		return this.getMcoAnamneseEfsDAO().listarAnamnesesEfsPorPacCodigo(
				pacCodigo);
	}

	@Override
	public List<McoAnamneseEfs> listarAnamnesesEfs(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoAnamneseEfsDAO().listarAnamnesesEfs(codigoPaciente,
				sequence);
	}
		
	@Override
	@BypassInactiveModule
	public List<HistObstetricaVO> pesquisarAnamnesesEfsPorGestacoesPaginada(
			Integer gsoPacCodigo, Short gsoSeqp, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.getMcoAnamneseEfsDAO()
				.pesquisarAnamnesesEfsPorGestacoesPaginada(gsoPacCodigo,
						gsoSeqp, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarAnamnesesEfsPorGestacoesPaginadaCount(
			Integer codigo, Short seqp) {
		return this.getMcoAnamneseEfsDAO().pesquisarAnamnesesEfsPorGestacoesPaginadaCount(codigo,seqp);
	}
	
	@Override
	public List<McoLogImpressoes> listarLogImpressoes(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoLogImpressoesDAO().listarLogImpressoes(
				codigoPaciente, sequence);
	}

	@Override
	public List<McoLogImpressoes> listarLogImpressoesPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoLogImpressoesDAO()
				.listarLogImpressoesPorCodigoPaciente(codigoPaciente);
	}

	@Override
	public List<McoPlanoIniciais> listarPlanosIniciais(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoPlanoIniciaisDAO().listarPlanosIniciais(codigoPaciente,
				 sequence);
	}

	@Override
	public List<McoPlanoIniciais> listarPlanosIniciaisPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoPlanoIniciaisDAO()
				.listarPlanosIniciaisPorCodigoPaciente(codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public List<McoBolsaRotas> listarBolsasRotas(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoBolsaRotasDAO().listarBolsasRotas(codigoPaciente,
				sequence);
	}

	@Override
	@BypassInactiveModule
	public List<McoBolsaRotas> listarBolsasRotasPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoBolsaRotasDAO().listarBolsasRotasPorCodigoPaciente(
				codigoPaciente);
	}

	@Override
	public List<McoResultadoExameSignifs> listarResultadosExamesSignifs(
			Integer codigoPaciente, Short sequence) {
		return this.getMcoResultadoExameSignifsDAO()
				.listarResultadosExamesSignifs(codigoPaciente, sequence);
	}

	@Override
	public List<McoResultadoExameSignifs> listarResultadosExamesSignifsPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoResultadoExameSignifsDAO()
				.listarResultadosExamesSignifsPorCodigoPaciente(codigoPaciente);
	}

	@Override
	public List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoes(
			Integer codigoPaciente, Short sequence) {
		return this.getMcoIntercorrenciaGestacoesDAO()
				.listarIntercorrenciasGestacoes(codigoPaciente, sequence);
	}

	@Override
	public List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoesPorCodigoPaciente(
			Integer codigoPaciente) {
		return this
				.getMcoIntercorrenciaGestacoesDAO()
				.listarIntercorrenciasGestacoesPorCodigoPaciente(codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartos(
			Integer codigoPaciente, Short sequence) {
		return this.getMcoMedicamentoTrabPartosDAO()
				.listarMedicamentosTrabPartos(codigoPaciente, sequence);
	}

	@Override
	@BypassInactiveModule
	public List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartosPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoMedicamentoTrabPartosDAO()
				.listarMedicamentosTrabPartosPorCodigoPaciente(codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public List<McoAtendTrabPartos> listarAtendTrabPartos(
			Integer codigoPaciente, Short sequence, McoAtendTrabPartos.Fields order) {
		return this.getMcoAtendTrabPartosDAO().listarAtendTrabPartos(
				codigoPaciente, sequence, order);
	}

	@Override
	public List<McoAtendTrabPartos> listarAtendTrabPartosPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoAtendTrabPartosDAO()
				.listarAtendTrabPartosPorCodigoPaciente(codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public List<McoTrabPartos> listarTrabPartos(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoTrabPartosDAO().listarTrabPartos(codigoPaciente,
				sequence);
	}

	@Override
	public List<McoTrabPartos> listarTrabPartosPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoTrabPartosDAO().listarTrabPartosPorCodigoPaciente(
				codigoPaciente);
	}

	@Override
	public List<McoIntercorrenciaNascs> listarIntercorrenciasNascs(
			Integer codigoPaciente, Short sequence) {
		return this.getMcoIntercorrenciaNascsDAO().listarIntercorrenciasNascs(
				codigoPaciente, sequence);
	}

	@Override
	public List<McoIntercorrenciaNascs> listarIntercorrenciasNascsPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoIntercorrenciaNascsDAO()
				.listarIntercorrenciasNascsPorCodigoPaciente(codigoPaciente);
	}

	@Override
	public List<McoProfNascs> listarProfNascs(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoProfNascsDAO().listarProfNascs(codigoPaciente,
				sequence);
	}

	@Override
	public List<McoProfNascs> listarProfNascsPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoProfNascsDAO().listarProfNascsPorCodigoPaciente(
				codigoPaciente);
	}

	@Override
	public List<McoForcipes> listarForcipes(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoForcipesDAO()
				.listarForcipes(codigoPaciente, sequence);
	}

	@Override
	public List<McoForcipes> listarForcipesPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoForcipesDAO().listarForcipesPorCodigoPaciente(
				codigoPaciente);
	}

	@Override
	public List<McoCesarianas> listarCesarianas(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoCesarianasDAO().listarCesarianas(codigoPaciente,
				sequence);
	}

	@Override
	public List<McoCesarianas> listarCesarianasPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoCesarianasDAO().listarCesarianasPorCodigoPaciente(
				codigoPaciente);
	}

	@Override
	public List<McoNascIndicacoes> listarNascIndicacoesPorForcipes(
			Integer forcipesCodigoPaciente, Short forcipesSequence) {
		return this.getMcoNascIndicacoesDAO().listarNascIndicacoesPorForcipes(
				forcipesCodigoPaciente, forcipesSequence);
	}

	@Override
	public List<McoNascIndicacoes> listarNascIndicacoesPorCesariana(
			Integer cesarianaCodigoPaciente, Short cesarianaSequence) {
		return this.getMcoNascIndicacoesDAO().listarNascIndicacoesPorCesariana(
				cesarianaCodigoPaciente, cesarianaSequence);
	}

	@Override
	public List<McoApgars> listarApgarsPorCodigoPaciente(Integer pacCodigo) {
		return this.getMcoApgarsDAO().listarApgarsPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<McoApgars> listarApgarsPorRecemNascido(
			Integer recemNascidoCodigoPaciente, Short recemNascidoSequence) {
		return this.getMcoApgarsDAO().listarApgarsPorRecemNascido(
				recemNascidoCodigoPaciente, recemNascidoSequence);
	}

	@Override
	public List<McoApgars> listarApgarsPorRecemNascidoCodigoPaciente(
			Integer recemNascidoCodigoPaciente) {
		return this.getMcoApgarsDAO()
				.listarApgarsPorRecemNascidoCodigoPaciente(
						recemNascidoCodigoPaciente);
	}
	
	@Override
	public List<McoExameFisicoRns> listarExamesFisicosRns(Integer gsoPacCodigo,
			Short gsoSeqp) {
		return this.getMcoExameFisicoRnsDAO().listarExamesFisicosRns(
				gsoPacCodigo, gsoSeqp);
	}

	@Override
	public List<McoExameFisicoRns> listarExamesFisicosRnsPorGestacoesCodigoPaciente(
			Integer gsoPacCodigo) {
		return this.getMcoExameFisicoRnsDAO()
				.listarExamesFisicosRnsPorGestacoesCodigoPaciente(gsoPacCodigo);
	}

	@Override
	public List<McoAchadoExameFisicos> listarAchadosExamesFisicos(
			Integer codigoPaciente, Short sequence) {
		return this.getMcoAchadoExameFisicosDAO().listarAchadosExamesFisicos(
				codigoPaciente, sequence);
	}

	@Override
	public List<McoAchadoExameFisicos> listarAchadosExamesFisicosPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoAchadoExameFisicosDAO()
				.listarAchadosExamesFisicosPorCodigoPaciente(codigoPaciente);
	}

	@Override
	public List<McoReanimacaoRns> listarReanimacoesRns(Integer pacCodigo,
			Short seq) {
		return this.getMcoReanimacaoRnsDAO().listarReanimacoesRns(pacCodigo,
				seq);
	}

	@Override
	public List<McoReanimacaoRns> listarReanimacoesRnsPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getMcoReanimacaoRnsDAO()
				.listarReanimacoesRnsPorCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<McoNascimentos> listarNascimentos(Integer codigoPaciente,
			Short sequence) {
		return this.getMcoNascimentosDAO().listarNascimentos(codigoPaciente,
				sequence);
	}

	@Override
	@BypassInactiveModule
	public List<McoNascimentos> listarNascimentosPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoNascimentosDAO().listarNascimentosPorCodigoPaciente(
				codigoPaciente);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoGestacoes> pesquisarMcoGestacoes(Integer pacCodigo) {
		return this.getMcoGestacoesDAO().pesquisarMcoGestacoes(pacCodigo);
	}

	@Override
	public Short obterMaxSeqMcoGestacoesComGravidezConfirmada(
			Integer pacCodigo, Byte gesta) {
		return this.getMcoGestacoesDAO()
				.obterMaxSeqMcoGestacoesComGravidezConfirmada(pacCodigo, gesta);
	}

	@Override
	public Short obterMaxSeqMcoGestacoes(Integer pacCodigo) {
		return this.getMcoGestacoesDAO().obterMaxSeqMcoGestacoes(pacCodigo);
	}
	
	@Override
	public List<McoGestacoes> listarGestacoesPorCodigoPacienteEGestacao(
			Integer pPacCodigoPara, Byte cGesta) {
		return this.getMcoGestacoesDAO()
				.listarGestacoesPorCodigoPacienteEGestacao(pPacCodigoPara,
						cGesta);
	}

	@Override
	public McoRecemNascidos obterMcoRecemNascidos(Integer pacCodigo) {
		return this.getMcoRecemNascidosDAO().obterMcoRecemNascidos(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<McoRecemNascidos> listarPorGestacao(Integer gsoPacCodigo,
			Short gsoSeqp) {
		return this.getMcoRecemNascidosDAO().listarPorGestacao(gsoPacCodigo,
				gsoSeqp);
	}

	@Override
	public McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente) {
		return this.getMcoRecemNascidosDAO().obterRecemNascidosPorCodigo(
				codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public Date obterDataNascimentoRecemNascidos(Integer codigoPaciente) {
		return this.getMcoRecemNascidosDAO().obterDataNascimentoRecemNascidos(
				codigoPaciente);
	}

	@Override
	public List<McoRecemNascidos> listarRecemNascidosPorCodigoPaciente(
			Integer codigoPaciente) {
		return this.getMcoRecemNascidosDAO()
				.listarRecemNascidosPorCodigoPaciente(codigoPaciente);
	}

	@Override
	public List<McoRecemNascidos> listarRecemNascidosPorGestacaoCodigoPaciente(
			Integer gsoPacCodigo) {
		return this.getMcoRecemNascidosDAO()
				.listarRecemNascidosPorGestacaoCodigoPaciente(gsoPacCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<McoRecemNascidos> pesquisarRecemNascidosPorCodigoPacienteSeqp(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return this.getMcoRecemNascidosDAO()
				.pesquisarRecemNascidosPorCodigoPacienteSeqp(gsoPacCodigo,
						gsoSeqp);
	}

	@Override
	public void atualizarMcoGestacoes(McoGestacoes mcoGestacoes, boolean flush) {
		McoGestacoesDAO mcoGestacoesDAO = this.getMcoGestacoesDAO();
		mcoGestacoesDAO.atualizar(mcoGestacoes);
		if (flush) {
			mcoGestacoesDAO.flush();
		}
	}

	@Override
	public void removerMcoIddGestCapurros(
			McoIddGestCapurros mcoIddGestCapurros, boolean flush) {
		McoIddGestCapurrosDAO mcoIddGestCapurrosDAO = this.getMcoIddGestCapurrosDAO();
		mcoIddGestCapurrosDAO.remover(mcoIddGestCapurros);
		if (flush) {
			mcoIddGestCapurrosDAO.flush();
		}
	}

	@Override
	public void removerMcoSnappes(McoSnappes mcoSnappes, boolean flush) {
		McoSnappesDAO mcoSnappesDAO = this.getMcoSnappesDAO();
		mcoSnappesDAO.remover(mcoSnappes);
		if (flush) {
			mcoSnappesDAO.flush();
		}
	}

	@Override
	public void removerMcoIddGestCapurros(
			McoIddGestBallards mcoIddGestBallards, boolean flush) {
		McoIddGestBallardsDAO mcoIddGestBallardsDAO = this.getMcoIddGestBallardsDAO();
		mcoIddGestBallardsDAO.remover(mcoIddGestBallards);
		if (flush) {
			mcoIddGestBallardsDAO.flush();
		}
	}

	@Override
	public void removerMcoLogImpressoes(McoLogImpressoes mcoLogImpressoes,
			boolean flush) {
		McoLogImpressoesDAO mcoLogImpressoesDAO = this.getMcoLogImpressoesDAO();
		mcoLogImpressoesDAO.remover(mcoLogImpressoes);
		if (flush) {
			mcoLogImpressoesDAO.flush();
		}
	}

	@Override
	public void removerMcoPlanoIniciais(McoPlanoIniciais mcoPlanoIniciais,
			boolean flush) {
		McoPlanoIniciaisDAO mcoPlanoIniciaisDAO = this.getMcoPlanoIniciaisDAO();
		mcoPlanoIniciaisDAO.remover(mcoPlanoIniciais);
		if (flush) {
			mcoPlanoIniciaisDAO.flush();
		}
	}

	@Override
	public void removerMcoGestacaoPacientes(
			McoGestacaoPacientes mcoGestacaoPacientes, boolean flush) {
		McoGestacaoPacientesDAO mcoGestacaoPacientesDAO = this.getMcoGestacaoPacientesDAO();
		mcoGestacaoPacientesDAO.remover(mcoGestacaoPacientes);
		if (flush) {
			mcoGestacaoPacientesDAO.flush();
		}
	}

	@Override
	public void removerMcoBolsaRotas(McoBolsaRotas mcoBolsaRotas, boolean flush) {
		McoBolsaRotasDAO mcoBolsaRotasDAO = this.getMcoBolsaRotasDAO();
		mcoBolsaRotasDAO.remover(mcoBolsaRotas);
		if (flush) {
			mcoBolsaRotasDAO.flush();
		}
	}

	@Override
	public void removerMcoResultadoExameSignifs(
			McoResultadoExameSignifs mcoResultadoExameSignifs, boolean flush) {
		McoResultadoExameSignifsDAO mcoResultadoExameSignifsDAO = this.getMcoResultadoExameSignifsDAO();
		mcoResultadoExameSignifsDAO.remover(mcoResultadoExameSignifs);
		if (flush) {
			mcoResultadoExameSignifsDAO.flush();
		}
	}

	@Override
	public void removerMcoIntercorrenciaGestacoes(
			McoIntercorrenciaGestacoes mcoIntercorrenciaGestacoes, boolean flush) {
		McoIntercorrenciaGestacoesDAO mcoIntercorrenciaGestacoesDAO = this.getMcoIntercorrenciaGestacoesDAO();
		mcoIntercorrenciaGestacoesDAO.remover(
				mcoIntercorrenciaGestacoes);
		if (flush) {
			mcoIntercorrenciaGestacoesDAO.flush();
		}
	}

	@Override
	public void removerMcoMedicamentoTrabPartos(
			McoMedicamentoTrabPartos mcoMedicamentoTrabPartos, boolean flush) {
		McoMedicamentoTrabPartosDAO mcoMedicamentoTrabPartosDAO = this.getMcoMedicamentoTrabPartosDAO();
		mcoMedicamentoTrabPartosDAO.remover(mcoMedicamentoTrabPartos);
		if (flush) {
			mcoMedicamentoTrabPartosDAO.flush();
		}
	}

	@Override
	public void removerMcoTrabPartos(McoTrabPartos mcoTrabPartos, boolean flush) {
		McoTrabPartosDAO mcoTrabPartosDAO = this.getMcoTrabPartosDAO();
		mcoTrabPartosDAO.remover(mcoTrabPartos);
		if (flush) {
			mcoTrabPartosDAO.flush();
		}
	}

	@Override
	public void removerMcoAtendTrabPartos(
			McoAtendTrabPartos mcoAtendTrabPartos, boolean flush) {
		McoAtendTrabPartosDAO mcoAtendTrabPartosDAO = this.getMcoAtendTrabPartosDAO();
		mcoAtendTrabPartosDAO.remover(mcoAtendTrabPartos);
		if (flush) {
			mcoAtendTrabPartosDAO.flush();
		}
	}

	@Override
	public void removerMcoIntercorrenciaNascs(
			McoIntercorrenciaNascs mcoIntercorrenciaNascs, boolean flush) {
		McoIntercorrenciaNascsDAO mcoIntercorrenciaNascsDAO = this.getMcoIntercorrenciaNascsDAO();
		mcoIntercorrenciaNascsDAO.remover(mcoIntercorrenciaNascs);
		if (flush) {
			mcoIntercorrenciaNascsDAO.flush();
		}
	}

	@Override
	public void removerMcoProfNascs(McoProfNascs mcoProfNascs, boolean flush) {
		McoProfNascsDAO mcoProfNascsDAO = this.getMcoProfNascsDAO();
		mcoProfNascsDAO.remover(mcoProfNascs);
		if (flush) {
			mcoProfNascsDAO.flush();
		}
	}

	@Override
	public void removerMcoForcipes(McoForcipes mcoForcipes, boolean flush) {
		McoForcipesDAO mcoForcipesDAO = this.getMcoForcipesDAO();
		mcoForcipesDAO.remover(mcoForcipes);
		if (flush) {
			mcoForcipesDAO.flush();
		}
	}

	@Override
	public void removerMcoCesarianas(McoCesarianas mcoCesarianas, boolean flush) {
		McoCesarianasDAO mcoCesarianasDAO = this.getMcoCesarianasDAO();
		mcoCesarianasDAO.remover(mcoCesarianas);
		if (flush) {
			mcoCesarianasDAO.flush();
		}
	}

	@Override
	public void removerMcoNascimentos(McoNascimentos mcoNascimentos,
			boolean flush) {
		McoNascimentosDAO mcoNascimentosDAO = this.getMcoNascimentosDAO();
		mcoNascimentosDAO.remover(mcoNascimentos);
		if (flush) {
			mcoNascimentosDAO.flush();
		}
	}

	@Override
	public void removerMcoReanimacaoRns(McoReanimacaoRns mcoReanimacaoRns,
			boolean flush) {
		McoReanimacaoRnsDAO mcoReanimacaoRnsDAO = this.getMcoReanimacaoRnsDAO();
		mcoReanimacaoRnsDAO.remover(mcoReanimacaoRns);
		if (flush) {
			mcoReanimacaoRnsDAO.flush();
		}
	}

	@Override
	public void removerMcoAchadoExameFisicos(
			McoAchadoExameFisicos mcoAchadoExameFisicos, boolean flush) {
		McoAchadoExameFisicosDAO mcoAchadoExameFisicosDAO = this.getMcoAchadoExameFisicosDAO();
		mcoAchadoExameFisicosDAO.remover(mcoAchadoExameFisicos);
		if (flush) {
			mcoAchadoExameFisicosDAO.flush();
		}
	}

	@Override
	public void removerMcoExameFisicoRns(McoExameFisicoRns mcoExameFisicoRns,
			boolean flush) {
		McoExameFisicoRnsDAO mcoExameFisicoRnsDAO = this.getMcoExameFisicoRnsDAO();
		mcoExameFisicoRnsDAO.remover(mcoExameFisicoRns);
		if (flush) {
			mcoExameFisicoRnsDAO.flush();
		}
	}

	@Override
	public void removerMcoApgars(McoApgars mcoApgars, boolean flush) {
		McoApgarsDAO mcoApgarsDAO = this.getMcoApgarsDAO();
		mcoApgarsDAO.remover(mcoApgars);
		if (flush) {
			mcoApgarsDAO.flush();
		}
	}

	@Override
	public void removerMcoRecemNascidos(McoRecemNascidos mcoRecemNascidos,
			boolean flush) {
		McoRecemNascidosDAO mcoRecemNascidosDAO = this.getMcoRecemNascidosDAO();
		mcoRecemNascidosDAO.remover(mcoRecemNascidos);
		if (flush) {
			mcoRecemNascidosDAO.flush();
		}
	}

	@Override
	public void removerMcoGestacoes(McoGestacoes mcoGestacoes, boolean flush) {
		McoGestacoesDAO mcoGestacoesDAO = this.getMcoGestacoesDAO();
		mcoGestacoesDAO.remover(mcoGestacoes);
		if (flush) {
			mcoGestacoesDAO.flush();
		}
	}

	@Override
	@BypassInactiveModule
	public McoGestacoes obterMcoGestacoesPorChavePrimaria(
			McoGestacoesId mcoGestacoesId) {
		return this.getMcoGestacoesDAO().obterPorChavePrimaria(mcoGestacoesId);
	}

	@Override
	public void persistirMcoGestacaoPacientes(
			McoGestacaoPacientes mcoGestacaoPacientes) {
		this.getMcoGestacaoPacientesDAO().persistir(mcoGestacaoPacientes);
	}

	@Override
	public void inserirMcoAnamneseEfsJn(McoAnamneseEfsJn mcoAnamneseEfsJn,
			boolean flush) {
		McoAnamneseEfsJnDAO mcoAnamneseEfsJnDAO = this.getMcoAnamneseEfsJnDAO();
		mcoAnamneseEfsJnDAO.persistir(mcoAnamneseEfsJn);
		if (flush) {
			mcoAnamneseEfsJnDAO.flush();
		}
	}

	@Override
	public void removerMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs) {
		McoAnamneseEfsDAO mcoAnamneseEfsDAO = this.getMcoAnamneseEfsDAO();
		mcoAnamneseEfsDAO.remover(mcoAnamneseEfs);
		mcoAnamneseEfsDAO.flush();
	}

	@Override
	public McoAnamneseEfs obterMcoAnamneseEfsPorChavePrimaria(
			McoAnamneseEfsId mcoAnamneseEfsId) {
		return this.getMcoAnamneseEfsDAO().obterPorChavePrimaria(
				mcoAnamneseEfsId);
	}

	@Override
	public void persistirMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs) {
		this.getMcoAnamneseEfsDAO().persistir(mcoAnamneseEfs);
	}

	@Override
	public void persistirMcoIddGestCapurros(
			McoIddGestCapurros mcoIddGestCapurros) {
		this.getMcoIddGestCapurrosDAO().persistir(mcoIddGestCapurros);
	}

	@Override
	public void persistirMcoIddGestBallards(
			McoIddGestBallards mcoIddGestBallards) {
		this.getMcoIddGestBallardsDAO().persistir(mcoIddGestBallards);
	}

	@Override
	public void persistirMcoSnappes(McoSnappes mcoSnappes) {
		this.getMcoSnappesDAO().persistir(mcoSnappes);
	}

	@Override
	public Boolean isSnappesPreenchido(Integer pacCodigo, Date inicioAtendimento) {
		return this.getMcoSnappesDAO().isSnappesPreenchido(pacCodigo, inicioAtendimento);
	}

	
	@Override
	public void atualizarMcoApgars(McoApgars mcoApgars, boolean flush) {
		McoApgarsDAO mcoApgarsDAO = this.getMcoApgarsDAO();
		mcoApgarsDAO.atualizar(mcoApgars);
		if (flush) {
			mcoApgarsDAO.flush();
		}
	}

	@Override
	public void persistirMcoReanimacaoRns(McoReanimacaoRns mcoReanimacaoRns) {
		this.getMcoReanimacaoRnsDAO().persistir(mcoReanimacaoRns);
	}

	@Override
	public void persistirMcoAchadoExameFisicos(
			McoAchadoExameFisicos mcoAchadoExameFisicos) {
		this.getMcoAchadoExameFisicosDAO().persistir(mcoAchadoExameFisicos);
	}

	@Override
	public void persistirMcoApgars(McoApgars mcoApgars) {
		this.getMcoApgarsDAO().persistir(mcoApgars);
	}

	@Override
	public void persistirMcoRecemNascidos(McoRecemNascidos mcoRecemNascidos) {
		this.getMcoRecemNascidosDAO().persistir(mcoRecemNascidos);
	}

	@Override
	public McoForcipes obterMcoForcipesPorChavePrimaria(
			McoNascimentosId mcoNascimentosId) {
		return this.getMcoForcipesDAO().obterPorChavePrimaria(mcoNascimentosId);
	}

	@Override
	@BypassInactiveModule
	public McoCesarianas obterMcoCesarianasPorChavePrimaria(
			McoNascimentosId mcoNascimentosId) {
		return this.getMcoCesarianasDAO().obterPorChavePrimaria(
				mcoNascimentosId);
	}

	@Override
	public void atualizarMcoCesarianas(McoCesarianas mcoCesarianas,
			boolean flush) {
		McoCesarianasDAO mcoCesarianasDAO = this.getMcoCesarianasDAO();
		mcoCesarianasDAO.atualizar(mcoCesarianas);
		if (flush) {
			mcoCesarianasDAO.flush();
		}
	}

	@Override
	public void persistirMcoCesarianas(McoCesarianas mcoCesarianas) {
		this.getMcoCesarianasDAO().persistir(mcoCesarianas);
	}

	@Override
	public void persistirMcoForcipes(McoForcipes mcoForcipes) {
		this.getMcoForcipesDAO().persistir(mcoForcipes);
	}

	@Override
	public void persistirMcoExameFisicoRns(McoExameFisicoRns mcoExameFisicoRns) {
		this.getMcoExameFisicoRnsDAO().persistir(mcoExameFisicoRns);
	}
	
	@Override
	public Boolean isExameFisicoRealizado(Integer pacCodigo) {
		return this.getMcoExameFisicoRnsDAO().isExameFisicoRealizado(pacCodigo);
	}
	
	@Override
	public void persistirMcoProfNascs(McoProfNascs mcoProfNascs) {
		this.getMcoProfNascsDAO().persistir(mcoProfNascs);
	}

	@Override
	public void persistirMcoIntercorrenciaNascs(
			McoIntercorrenciaNascs mcoIntercorrenciaNascs) {
		this.getMcoIntercorrenciaNascsDAO().persistir(mcoIntercorrenciaNascs);
	}

	@Override
	public void persistirMcoNascimentos(McoNascimentos mcoNascimentos) {
		this.getMcoNascimentosDAO().persistir(mcoNascimentos);
	}

	@Override
	public void persistirMcoTrabPartos(McoTrabPartos mcoTrabPartos) {
		this.getMcoTrabPartosDAO().persistir(mcoTrabPartos);
	}

	@Override
	public void persistirMcoAtendTrabPartos(
			McoAtendTrabPartos mcoAtendTrabPartos) {
		this.getMcoAtendTrabPartosDAO().persistir(mcoAtendTrabPartos);
	}

	@Override
	public void persistirMcoMedicamentoTrabPartos(
			McoMedicamentoTrabPartos mcoMedicamentoTrabPartos) {
		this.getMcoMedicamentoTrabPartosDAO().persistir(
				mcoMedicamentoTrabPartos);
	}

	@Override
	public void persistirMcoIntercorrenciaGestacoes(
			McoIntercorrenciaGestacoes mcoIntercorrenciaGestacoes) {
		this.getMcoIntercorrenciaGestacoesDAO().persistir(
				mcoIntercorrenciaGestacoes);
	}

	@Override
	public void inserirMcoGestacoes(McoGestacoes mcoGestacoes, boolean flush) {
		McoGestacoesDAO mcoGestacoesDAO = this.getMcoGestacoesDAO();
		mcoGestacoesDAO.persistir(mcoGestacoes);
		if (flush) {
			mcoGestacoesDAO.flush();
		}
	}

	@Override
	public void persistirMcoResultadoExameSignifs(
			McoResultadoExameSignifs mcoResultadoExameSignifs) {
		this.getMcoResultadoExameSignifsDAO().persistir(
				mcoResultadoExameSignifs);
	}

	@Override
	public void persistirMcoBolsaRotas(McoBolsaRotas mcoBolsaRotas) {
		this.getMcoBolsaRotasDAO().persistir(mcoBolsaRotas);
	}

	@Override
	public void persistirMcoLogImpressoes(McoLogImpressoes mcoLogImpressoes) {
		this.getMcoLogImpressoesDAO().persistir(mcoLogImpressoes);
	}

	@Override
	public Boolean isImpressaoLogNecessaria(Integer pacCodigo) {
		return this.getMcoLogImpressoesDAO().isImpressaoLogNecessaria(pacCodigo);
	}
	
	@Override
	public Boolean isImpressaoRelatorioFisicoNecessaria(Integer pacCodigo) {
		return this.getMcoLogImpressoesDAO().isImpressaoRelatorioFisicoNecessaria(pacCodigo);
	}
	
	@Override
	public void persistirMcoPlanoIniciais(McoPlanoIniciais mcoPlanoIniciais) {
		this.getMcoPlanoIniciaisDAO().persistir(mcoPlanoIniciais);
	}

	@Override
	public void desatacharMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs) {
		this.getMcoAnamneseEfsDAO().desatachar(mcoAnamneseEfs);
	}

	@Override
	public McoEscalaLeitoRns obterMcoEscalaLeitoRnsPorLeito(String leitoID) {
		return this.getMcoEscalaLeitoRnsDAO().obterMcoEscalaLeitoRnsPorLeito(
				leitoID);
	}

	@Override
	public List<McoPartos> listarPartosPorCodigoPaciente(Integer codigoOrigem) {
		return this.getMcoPartosDAO().listarPartosPorCodigoPaciente(
				codigoOrigem);
	}

	@Override
	public void persistirMcoPartos(McoPartos mcoPartos) {
		this.getMcoPartosDAO().persistir(mcoPartos);
	}

	@Override
	@BypassInactiveModule
	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorConsultaObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		return this.getMcoLogImpressoesDAO()
				.pesquisarLogImpressoesEventoMcorConsultaObs(gsoPacCodigo,
						gsoSeqp, conNumero);
	}

	@Override
	@BypassInactiveModule
	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorAdmissaoObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		return this.getMcoLogImpressoesDAO()
				.pesquisarLogImpressoesEventoMcorAdmissaoObs(gsoPacCodigo,
						gsoSeqp, conNumero);
	}

	@Override
	@BypassInactiveModule
	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorNascimento(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return this.getMcoLogImpressoesDAO()
				.pesquisarLogImpressoesEventoMcorNascimento(gsoPacCodigo,
						gsoSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorRnSlParto(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return this.getMcoLogImpressoesDAO()
				.pesquisarLogImpressoesEventoMcorRnSlParto(gsoPacCodigo,
						gsoSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorExFisicoRn(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return this.getMcoLogImpressoesDAO()
				.pesquisarLogImpressoesEventoMcorExFisicoRn(gsoPacCodigo,
						gsoSeqp);
	}

	protected PerinatologiaON getPerinatologiaON() {
		return perinatologiaON;
	}

	protected McoRecemNascidosDAO getMcoRecemNascidosDAO() {
		return mcoRecemNascidosDAO;
	}
	
	protected McoGestacoesDAO getMcoGestacoesDAO() {
		return mcoGestacoesDAO;
	}

	protected McoNascimentosDAO getMcoNascimentosDAO() {
		return mcoNascimentosDAO;
	}

	protected McoReanimacaoRnsDAO getMcoReanimacaoRnsDAO() {
		return mcoReanimacaoRnsDAO;
	}
	
	protected McoAchadoDAO getMcoAchadoDAO() {
		return mcoAchadoDAO;
	}
	
	protected McoAchadoExameFisicosDAO getMcoAchadoExameFisicosDAO() {
		return mcoAchadoExameFisicosDAO;
	}

	protected McoExameFisicoRnsDAO getMcoExameFisicoRnsDAO() {
		return mcoExameFisicoRnsDAO;
	}

	protected McoSindromeDAO getMcoSindromeDAO() {
		return mcoSindromeDAO;
	}
	
	protected McoApgarsDAO getMcoApgarsDAO() {
		return mcoApgarsDAO;
	}

	protected McoNascIndicacoesDAO getMcoNascIndicacoesDAO() {
		return mcoNascIndicacoesDAO;
	}

	protected McoCesarianasDAO getMcoCesarianasDAO() {
		return mcoCesarianasDAO;
	}

	protected McoForcipesDAO getMcoForcipesDAO() {
		return mcoForcipesDAO;
	}

	protected McoProfNascsDAO getMcoProfNascsDAO() {
		return mcoProfNascsDAO;
	}

	protected McoIntercorrenciaNascsDAO getMcoIntercorrenciaNascsDAO() {
		return mcoIntercorrenciaNascsDAO;
	}

	protected McoTrabPartosDAO getMcoTrabPartosDAO() {
		return mcoTrabPartosDAO;
	}

	protected McoAtendTrabPartosDAO getMcoAtendTrabPartosDAO() {
		return mcoAtendTrabPartosDAO;
	}

	protected McoGestacoes getMcoGestacoes() {
		return new McoGestacoes();
	}
	
	protected McoMedicamentoTrabPartosDAO getMcoMedicamentoTrabPartosDAO() {
		return mcoMedicamentoTrabPartosDAO;
	}

	protected McoIntercorrenciaGestacoesDAO getMcoIntercorrenciaGestacoesDAO() {
		return mcoIntercorrenciaGestacoesDAO;
	}

	protected McoResultadoExameSignifsDAO getMcoResultadoExameSignifsDAO() {
		return mcoResultadoExameSignifsDAO;
	}

	protected McoBolsaRotasDAO getMcoBolsaRotasDAO() {
		return mcoBolsaRotasDAO;
	}

	protected McoPlanoIniciaisDAO getMcoPlanoIniciaisDAO() {
		return mcoPlanoIniciaisDAO;
	}

	protected McoLogImpressoesDAO getMcoLogImpressoesDAO() {
		return mcoLogImpressoesDAO;
	}

	protected McoAnamneseEfsDAO getMcoAnamneseEfsDAO() {
		return mcoAnamneseEfsDAO;
	}

	protected McoAnamneseEfsJnDAO getMcoAnamneseEfsJnDAO() {
		return mcoAnamneseEfsJnDAO;
	}

	protected McoGestacaoPacientesDAO getMcoGestacaoPacientesDAO() {
		return mcoGestacaoPacientesDAO;
	}

	protected McoIddGestCapurrosDAO getMcoIddGestCapurrosDAO() {
		return mcoIddGestCapurrosDAO;
	}

	protected McoIddGestBallardsDAO getMcoIddGestBallardsDAO() {
		return mcoIddGestBallardsDAO;
	}

	protected McoSnappesDAO getMcoSnappesDAO() {
		return mcoSnappesDAO;
	}

	protected McoEscalaLeitoRnsDAO getMcoEscalaLeitoRnsDAO() {
		return mcoEscalaLeitoRnsDAO;
	}
	
	protected McoPartosDAO getMcoPartosDAO() {
		return mcoPartosDAO;
	}
	
	protected McoIndicacaoNascimentoDAO getMcoIndicacaoNascimentoDAO() {
		return mcoIndicacaoNascimentoDAO;
	}
	
	protected McoIntercorPasatusDAO getMcoIntercorPasatusDAO() {
		return mcoIntercorPasatusDAO;
	}
	
	protected VMcoExamesDAO getVMcoExamesDAO() {
		return vMcoExamesDAO;
	}
	
	protected McoNotaAdicionalDAO getMcoNotaAdicionalDAO() {
		return mcoNotaAdicionalDAO;
	}
	
	protected McoProcReanimacaoDAO getMcoProcReanimacaoDAO() {
		return mcoProcReanimacaoDAO;
	}

	@Override
	@BypassInactiveModule
	public boolean verificarExisteGestacao(Integer pacCodigo) {		
		return this.getMcoGestacoesDAO().verificarExisteGestacao(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public McoForcipes obterForcipe(Integer codigoPaciente, Short gsoSeqp, Integer seqp) {
		return getMcoForcipesDAO().obterForcipe(codigoPaciente, gsoSeqp, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoResultadoExameSignifs> listarResultadosExamesSignifsPorCodigoPacienteSeqpGestacao(
			Integer codigoPaciente, Short sequence) {
		return this.getMcoResultadoExameSignifsDAO()
				.listarResultadosExamesSignifsPorGestacao(codigoPaciente, sequence);
	}

	@Override
	@BypassInactiveModule
	public McoAtendTrabPartos obterAtendTrabPartos(Integer codigoPaciente, Short sequence) {
		return getMcoAtendTrabPartosDAO().obterAtendTrabPartos(codigoPaciente, sequence);
	}

	@Override
	@BypassInactiveModule
	public McoIndicacaoNascimento obterMcoIndicacaoNascimentoPorChavePrimaria(Integer seq) {
		return getMcoIndicacaoNascimentoDAO().obterOriginal(seq);
		
	}
	
	@BypassInactiveModule
	public List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoesPorCodGestCodPaciente(Short gsoSeq, 
																								Integer gsoPacCodigo, 
																								DominioOcorrenciaIntercorrenciaGestacao dominioOcorrenciaIntercorrenciaGestacao) {
		return this.getMcoIntercorrenciaGestacoesDAO().listarIntercorrenciasGestacoesPorCodGestCodPaciente(gsoSeq, gsoPacCodigo, dominioOcorrenciaIntercorrenciaGestacao);
	}
	
	@BypassInactiveModule
	public List<McoIntercorPasatus> listarIntercorPasatusPorSeq(Integer ingOpaSeq) {
		return this.getMcoIntercorPasatusDAO().listarIntercorPasatusPorSeq(ingOpaSeq);
	}
	
	@Override
	@BypassInactiveModule
	public McoIntercorPasatus obterMcoIntercorPasatusPorChavePrimaria(Integer seq) {
		return getMcoIntercorPasatusDAO().obterPorChavePrimaria(seq);
	}
	
	@BypassInactiveModule	
	public List<McoNascIndicacoes> listarNascIndicacoesPorCesariana(Integer cesPacCod, Integer cesSeq, Short cesGsoSeqp) {
		return getMcoNascIndicacoesDAO().listarNascIndicacoesPorCesariana(cesPacCod, cesSeq, cesGsoSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public VMcoExames obterVMcoExamesPorChave(String chave) {
		return getVMcoExamesDAO().obterVMcoExamesPorChave(chave);
	}

	@Override
	@BypassInactiveModule
	public McoNotaAdicional obterMcoNotaAdicional(Integer pacCodigo, Short gsoSeqp, Byte seqp, Integer conNumero, DominioEventoNotaAdicional evento) {
		return getMcoNotaAdicionalDAO().obterMcoNotaAdicional(pacCodigo, gsoSeqp, seqp, conNumero, evento);
	}
	
	@Override
	@BypassInactiveModule
	public McoProcReanimacao obterMcoProcReanimacaoPorId(Integer seq) {
		return getMcoProcReanimacaoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public McoAnamneseEfs obterAnamnesePorGestacaoEConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		return getMcoAnamneseEfsDAO().obterAnamnesePorGestacaoEConsulta(pacCodigo, gsoSeqp, conNumero);
	}

	@Override
	@BypassInactiveModule
	public McoAnamneseEfs obterAnamnesePorPacienteSequenceGestacaoConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		return getMcoAnamneseEfsDAO().obterAnamnesePorPacienteSequenceGestacaoConsulta(pacCodigo, gsoSeqp, conNumero);
	}
	
	@Override
	@BypassInactiveModule
	public McoAtendTrabPartos obterAtendTrabPartosMaxSeqp(Integer pacCodigo, Short gsoSeqp) {
		return getMcoAtendTrabPartosDAO().obterAtendTrabPartosMaxSeqp(pacCodigo, gsoSeqp);
	}

	@Override
	@BypassInactiveModule
	public McoIndicacaoNascimento obterIndicacaoNascimentoPorChavePrimaria(Integer inaSeq) {
		return getMcoIndicacaoNascimentoDAO().obterPorChavePrimaria(inaSeq);
	}

	@Override
	@BypassInactiveModule
	public List<McoIntercorrenciaNascs> listarIntercorrenciaNascPorCodSequenceSeq(
			Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		return getMcoIntercorrenciaNascsDAO().listarIntercorrenciaNascPorCodSequenceSeq(gsoPacCodigo, gsoSeqp, seqp);
	}

	@Override
	@BypassInactiveModule
	public List<McoNascIndicacoes> pesquisarNascIndicacoesPorForcipes(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		return getMcoNascIndicacoesDAO().pesquisarNascIndicacoesPorForcipes(gsoPacCodigo, gsoSeqp, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public McoCesarianas obterCesarianaPorChavePrimaria(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		return this.getMcoCesarianasDAO().obterPorChavePrimaria(gsoPacCodigo,gsoSeqp,seqp);
	}

	@Override
	@BypassInactiveModule
	public McoIntercorrencia obterIntercorrenciaoPorChavePrimaria(Short icrSeq) {
		return this.getMcoIntercorrenciaDAO().obterPorChavePrimaria(icrSeq);
	}

	protected McoIntercorrenciaDAO getMcoIntercorrenciaDAO() {
		return mcoIntercorrenciaDAO;
	}

	@Override
	@BypassInactiveModule
	public McoProcedimentoObstetrico obterProcedimentoObstetricoPorChavePrimaria(Short obsSeq) {
		return this.getMcoProcedimentoObstetricoDAO().obterPorChavePrimaria(obsSeq);
	}
	
	protected McoProcedimentoObstetricoDAO getMcoProcedimentoObstetricoDAO() {
		return mcoProcedimentoObstetricoDAO;
	}
	@Override
	@BypassInactiveModule
	public McoBolsaRotas obterMcoBolsaRotasProId(McoGestacoesId id) {
		return getMcoBolsaRotasDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoPlanoIniciais> listarPlanosIniciaisPorPacienteSequenceNumeroConsulta(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		return getMcoPlanoIniciaisDAO().listarPlanosIniciaisPorPacienteSequenceNumeroConsulta(gsoPacCodigo, gsoSeqp, conNumero);
	}
	
	protected McoCondutaDAO getMcoCondutaDAO() {
		return mcoCondutaDAO;
	}
	
	@Override
	@BypassInactiveModule
	public McoConduta obterMcoCondutaPorChavePrimaria(Long cdtSeq) {
		return getMcoCondutaDAO().obterPorChavePrimaria(cdtSeq);
	}

	@Override
	@BypassInactiveModule
	public List<McoProfNascs> listarProfNascsPorNascimento(Integer pacCodigo, Short gsoSeqp, Integer nasSeqp) {
		return this.getMcoProfNascsDAO().listarProfNascsPorNascimento(pacCodigo,gsoSeqp,nasSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<McoNotaAdicional> pesquisarNotaAdicionalPorPacienteGestacaoEvento(Integer pacCodigo, Short gsoSeqp) {
		return getMcoNotaAdicionalDAO().pesquisarNotaAdicionalPorPacienteGestacaoEvento(pacCodigo, gsoSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<McoNascimentos> pesquisarNascimentosPorGestacao(Integer pacCodigo, Short gsoSeqp) {
		return this.getMcoNascimentosDAO().pesquisarNascimentosPorGestacao(pacCodigo, gsoSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoNotaAdicional> pesquisarNotaAdicionalPorPacienteGestacaoConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero) {
		return getMcoNotaAdicionalDAO().pesquisarNotaAdicionalPorPacienteGestacaoConsulta(pacCodigo, gsoSeqp, conNumero);
	}

	@Override
	@BypassInactiveModule
	public Date obterMaxCriadoEmMcoGestacoes(Integer pacCodigo, Byte gesta, Date criadoEm) {
		return this.getMcoGestacoesDAO().obterMaxCriadoEmMcoGestacoes(pacCodigo, gesta, criadoEm);
	}

	@Override
	@BypassInactiveModule
	public List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartos(
			Integer codigoPaciente, Short sequence, Fields atributoNotNull,
			Fields order, Boolean asc, Integer... codsMdtos) {
		return getMcoMedicamentoTrabPartosDAO().listarMedicamentosTrabPartos(codigoPaciente, sequence, atributoNotNull, order, asc, codsMdtos);
	}

	@Override
	@BypassInactiveModule
	public List<McoAtendTrabPartos> listarAtendTrabPartos(Integer pacCodigo,
			Short gsoSeqp,
			McoAtendTrabPartos.Fields order,
			Boolean indAnalgesiaBpd, Boolean indAnalgesiaBsd) {
		return getMcoAtendTrabPartosDAO().listarAtendTrabPartos(pacCodigo, gsoSeqp, order, indAnalgesiaBpd, indAnalgesiaBsd);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(Integer pacCodigo, Short seqp) {
		return getMcoRecemNascidosDAO().pesquisarMcoRecemNascidoPorGestacaoOrdenado(pacCodigo, seqp);
	}

	@Override
	@BypassInactiveModule
	public McoAnamneseEfs obterAnamnesePorPacienteGsoSeqpConNumero(Integer pacCodigo, Short gsoSeqp,
			Integer conNumero) {
		return getMcoAnamneseEfsDAO().obterAnamnesePorPacienteGsoSeqpConNumero(pacCodigo, gsoSeqp, conNumero);
	}

	@Override
	@BypassInactiveModule
	public McoRecemNascidos obterRecemNascidoPorPacCodigoGsoSeqpSeqp(Integer pacCodigo, Short gsoSeqp, Byte seqp) {
		return getMcoRecemNascidosDAO().obterRecemNascidoPorPacCodigoGsoSeqpSeqp(pacCodigo, gsoSeqp, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public McoExameFisicoRns obterMcoExameFisicoRnsPorChavePrimaria(final Integer pacCodigo, final Short gsoSeqp, final Byte seqp) {
		return getMcoExameFisicoRnsDAO().obterPorChavePrimaria(new McoRecemNascidosId(pacCodigo, gsoSeqp, seqp));
	}
	
	@Override
	@BypassInactiveModule
	public McoSindrome obterMcoSindromePorChavePrimaria(Integer seq) {
		return getMcoSindromeDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoAchadoExameFisicos> listarAchadosExamesFisicosPorCodigoPacienteGsoSeqpSepq(
			Integer pacCodigo, Short gsoSeqp, Byte seqp) {
		return getMcoAchadoExameFisicosDAO().listarAchadosExamesFisicosPorCodigoPacienteGsoSeqpSepq(pacCodigo, gsoSeqp, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public McoAchado obterMcoAchadoObterPorChavePrimaria(Integer acdSeq) {
		return getMcoAchadoDAO().obterPorChavePrimaria(acdSeq);
	}

	@Override
	@BypassInactiveModule
	public McoNotaAdicional pesquisarNotaAdicionalPorPacienteGestacaoSeqp(
			Integer pacCodigo, Short gsoSeqp, Byte seqp) {
		return getMcoNotaAdicionalDAO().obterNotaAdicionalPorPacienteGestacaoSeqp(pacCodigo, gsoSeqp, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<McoRecemNascidos> pesquisarMcoRecemNascidoByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		return getMcoRecemNascidosDAO().pesquisarMcoRecemNascidoByMbcFichaAnestesia(seqMbcFichaAnestesia);
	}

	@Override
	@BypassInactiveModule
	public List<String> obterDescricaoMcoIndicacaoNascimentoByFichaAnestesia(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return getMcoIndicacaoNascimentoDAO().obterDescricaoMcoIndicacaoNascimentoByFichaAnestesia(gsoPacCodigo, gsoSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<InformacoesPerinataisVO> pesquisarInformacoesPerinataisCodigoPaciente(Integer pacCodigo) {
		return getMcoGestacoesDAO().pesquisarInformacoesPerinataisCodigoPaciente(pacCodigo);
	}

	@Override
	@BypassInactiveModule
	public boolean verificarExisteRecemNascido(Integer pacCodigo){
		return getMcoRecemNascidosDAO().verificarExisteRecemNascido(pacCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public McoAnamneseEfs obterAnamnesePorPaciente(Integer pacCodigo, Short gsoSeqp) {
		return getMcoAnamneseEfsDAO().obterAnamnesePorPaciente(pacCodigo, gsoSeqp);
	}

	@Override
	@BypassInactiveModule
	public List<McoNotaAdicional> pesquisarMcoNotaAdicional(Integer pacCodigo,
			Short gsoSeqp, Integer conNumero,
			DominioEventoNotaAdicional evento,
			McoNotaAdicional.Fields... orders) {
		return getMcoNotaAdicionalDAO().pesquisarMcoNotaAdicional(pacCodigo,
				gsoSeqp, conNumero, evento, orders);
	}
	
    @Override
    @BypassInactiveModule
    public boolean verificarExisteAtendimentoGestacaoUnidadeFuncional(Integer atdSeq,Short unfSeq ) {
        return this.getMcoGestacoesDAO().verificarExisteAtendimentoGestacaoUnidadeFuncional(atdSeq,unfSeq);
    }

    @Override
    @BypassInactiveModule
    public McoGestacoes obterUltimaGestacaoGE(Integer pacCodigo) {
        return getMcoGestacoesDAO().obterUltimaGestacaoGE(pacCodigo);
    }

    @Override
    @BypassInactiveModule
    public List<McoSnappes> listarSnappesPorCodigoPacienteDthrInternacao(Integer pacCodigo,Date dthrInternacao) {
        return getMcoSnappesDAO().listarSnappesPorCodigoPacienteDthrInternacao(pacCodigo,dthrInternacao);
    }

    /* (non-Javadoc)
    * @see br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade#atualizarMcoSnappes(br.gov.mec.aghu.model.McoRecemNascidos, boolean)
    */
    @Override
    public void atualizarMcoSnappes(final McoSnappes mcoSnappes, final boolean flush) {
        McoSnappesDAO snappesDAO = this.getMcoSnappesDAO();
        snappesDAO.persistir(mcoSnappes);
        if (flush) {
            snappesDAO.flush();
        }
    }


	@Override
	public McoNascimentos obterMcoNascimento(Integer gsoPacCodigo, Short gsoSeqp,
			Date dthrInternacao, Date dataInternacaoAdministrativa) {
		
		return this.mcoNascimentosDAO.obterMcoNascimento(gsoPacCodigo, gsoSeqp, dthrInternacao, dataInternacaoAdministrativa);
		
	}
	
	@Override
	public List<RelatorioSnappeVO> listarRelatorioSnappe2(Integer pacCodigoRecemNascido, Short seqp, Integer gsoPacCodigo, Short gsoSeqp, Byte recemNascidoSeqp) throws ApplicationBusinessException {
		return getPerinatologiaON().listarRelatorioSnappe2(pacCodigoRecemNascido, seqp, gsoPacCodigo, gsoSeqp, recemNascidoSeqp);
	}
}	