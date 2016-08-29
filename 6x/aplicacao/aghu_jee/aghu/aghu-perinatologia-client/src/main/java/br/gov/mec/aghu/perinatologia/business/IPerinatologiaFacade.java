package br.gov.mec.aghu.perinatologia.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioOcorrenciaIntercorrenciaGestacao;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.model.McoAchadoExameFisicos;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsId;
import br.gov.mec.aghu.model.McoAnamneseEfsJn;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartos.Fields;
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
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.paciente.prontuario.vo.InformacoesPerinataisVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;
import br.gov.mec.aghu.perinatologia.vo.RelatorioSnappeVO;
import br.gov.mec.aghu.view.VMcoExames;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface IPerinatologiaFacade extends Serializable {

	/**
	 * ORADB MCOT_PLR_BRI, MCOT_PLR_BRU
	 * 
	 * @param placar
	 *            Retirar esta annotation de  quando as
	 *            triggers de MCO_PLACARS forem implementadas em java
	 */
	
	void persistirPlacar(final McoPlacar placar);

	
	McoPlacar buscarPlacar(final Integer codigoPaciente);

	
	List<McoRecemNascidos> listaRecemNascidosPorCodigoPaciente(
			final Integer codigoPaciente);

	McoGestacoes obterMcoGestacoes(final McoGestacoesId id);

	McoRecemNascidos obterRecemNascidoGestacoesPaciente(
			final Integer pPAcCodigo, final Short pGsoSeqp,
			final Integer pSeqp);

	void inserirMcoRecemNascidos(final McoRecemNascidos mcoRecemNascidos,
			final boolean flush);

	void atualizarMcoRecemNascidos(final McoRecemNascidos mcoRecemNascidos,
			final boolean flush);

	McoNascimentos obterMcoNascimento(final Integer seqp,
			final Integer codigoPaciente, final Short sequence);

	List<McoSnappes> listarSnappesPorCodigoPaciente(Integer pacCodigo);

	List<McoIddGestBallards> listarIddGestBallardsPorCodigoPaciente(
			Integer pacCodigo);

	List<McoIddGestCapurros> listarIddGestCapurrosPorCodigoPaciente(
			Integer pacCodigo);

	List<McoGestacaoPacientes> listarGestacoesPacientePorCodigoPaciente(
			Integer pacCodigo);

	List<McoAnamneseEfs> listarAnamnesesEfsPorPacCodigo(Integer pacCodigo);

	List<McoAnamneseEfs> listarAnamnesesEfs(Integer codigoPaciente,
			Short sequence);

	List<HistObstetricaVO> pesquisarAnamnesesEfsPorGestacoesPaginada(
			Integer gsoPacCodigo, Short gsoSeqp, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);
	
	Long pesquisarAnamnesesEfsPorGestacoesPaginadaCount(Integer codigo,
			Short seqp);

	List<McoLogImpressoes> listarLogImpressoes(Integer codigoPaciente,
			Short sequence);

	List<McoLogImpressoes> listarLogImpressoesPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoPlanoIniciais> listarPlanosIniciais(Integer codigoPaciente,
			Short sequence);

	List<McoPlanoIniciais> listarPlanosIniciaisPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoBolsaRotas> listarBolsasRotas(Integer codigoPaciente, Short sequence);

	List<McoBolsaRotas> listarBolsasRotasPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoResultadoExameSignifs> listarResultadosExamesSignifs(
			Integer codigoPaciente, Short sequence);

	List<McoResultadoExameSignifs> listarResultadosExamesSignifsPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoes(
			Integer codigoPaciente, Short sequence);

	List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoesPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartos(
			Integer codigoPaciente, Short sequence);

	List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartosPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoAtendTrabPartos> listarAtendTrabPartos(Integer codigoPaciente,
			Short sequence, McoAtendTrabPartos.Fields order);

	List<McoAtendTrabPartos> listarAtendTrabPartosPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoTrabPartos> listarTrabPartos(Integer codigoPaciente, Short sequence);

	List<McoTrabPartos> listarTrabPartosPorCodigoPaciente(Integer codigoPaciente);

	List<McoIntercorrenciaNascs> listarIntercorrenciasNascs(
			Integer codigoPaciente, Short sequence);

	List<McoIntercorrenciaNascs> listarIntercorrenciasNascsPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoProfNascs> listarProfNascs(Integer codigoPaciente, Short sequence);

	List<McoProfNascs> listarProfNascsPorCodigoPaciente(Integer codigoPaciente);

	List<McoForcipes> listarForcipes(Integer codigoPaciente, Short sequence);

	List<McoForcipes> listarForcipesPorCodigoPaciente(Integer codigoPaciente);

	List<McoCesarianas> listarCesarianas(Integer codigoPaciente, Short sequence);

	List<McoCesarianas> listarCesarianasPorCodigoPaciente(Integer codigoPaciente);

	List<McoNascIndicacoes> listarNascIndicacoesPorForcipes(
			Integer forcipesCodigoPaciente, Short forcipesSequence);

	List<McoNascIndicacoes> listarNascIndicacoesPorCesariana(
			Integer cesarianaCodigoPaciente, Short cesarianaSequence);

	List<McoApgars> listarApgarsPorCodigoPaciente(Integer pacCodigo);

	List<McoApgars> listarApgarsPorRecemNascido(
			Integer recemNascidoCodigoPaciente, Short recemNascidoSequence);

	List<McoApgars> listarApgarsPorRecemNascidoCodigoPaciente(
			Integer recemNascidoCodigoPaciente);

	List<McoExameFisicoRns> listarExamesFisicosRns(Integer gsoPacCodigo,
			Short gsoSeqp);

	List<McoExameFisicoRns> listarExamesFisicosRnsPorGestacoesCodigoPaciente(
			Integer gsoPacCodigo);

	List<McoAchadoExameFisicos> listarAchadosExamesFisicos(
			Integer codigoPaciente, Short sequence);

	List<McoAchadoExameFisicos> listarAchadosExamesFisicosPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoReanimacaoRns> listarReanimacoesRns(Integer pacCodigo, Short seq);

	List<McoReanimacaoRns> listarReanimacoesRnsPorCodigoPaciente(
			Integer pacCodigo);

	List<McoNascimentos> listarNascimentos(Integer codigoPaciente,
			Short sequence);

	List<McoNascimentos> listarNascimentosPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoGestacoes> pesquisarMcoGestacoes(Integer pacCodigo);

	Short obterMaxSeqMcoGestacoesComGravidezConfirmada(Integer pacCodigo,
			Byte gesta);

	List<McoGestacoes> listarGestacoesPorCodigoPacienteEGestacao(
			Integer pPacCodigoPara, Byte cGesta);

	McoRecemNascidos obterMcoRecemNascidos(Integer pacCodigo);

	List<McoRecemNascidos> listarPorGestacao(Integer gsoPacCodigo, Short gsoSeqp);

	McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente);

	Date obterDataNascimentoRecemNascidos(Integer codigoPaciente);

	List<McoRecemNascidos> listarRecemNascidosPorCodigoPaciente(
			Integer codigoPaciente);

	List<McoRecemNascidos> listarRecemNascidosPorGestacaoCodigoPaciente(
			Integer gsoPacCodigo);

	List<McoRecemNascidos> pesquisarRecemNascidosPorCodigoPacienteSeqp(
			Integer gsoPacCodigo, Short gsoSeqp);

	void atualizarMcoGestacoes(McoGestacoes mcoGestacoes, boolean flush);

	void removerMcoIddGestCapurros(McoIddGestCapurros mcoIddGestCapurros,
			boolean flush);

	void removerMcoSnappes(McoSnappes mcoSnappes, boolean flush);

	void removerMcoIddGestCapurros(McoIddGestBallards mcoIddGestBallards,
			boolean flush);

	void removerMcoLogImpressoes(McoLogImpressoes mcoLogImpressoes,
			boolean flush);

	void removerMcoPlanoIniciais(McoPlanoIniciais mcoPlanoIniciais,
			boolean flush);

	void removerMcoGestacaoPacientes(McoGestacaoPacientes mcoGestacaoPacientes, boolean flush);

	void removerMcoBolsaRotas(McoBolsaRotas mcoBolsaRotas, boolean flush);

	void removerMcoResultadoExameSignifs(
			McoResultadoExameSignifs mcoResultadoExameSignifs, boolean flush);

	void removerMcoIntercorrenciaGestacoes(
			McoIntercorrenciaGestacoes mcoIntercorrenciaGestacoes, boolean flush);

	void removerMcoMedicamentoTrabPartos(
			McoMedicamentoTrabPartos mcoMedicamentoTrabPartos, boolean flush);

	void removerMcoTrabPartos(McoTrabPartos mcoTrabPartos, boolean flush);

	void removerMcoAtendTrabPartos(McoAtendTrabPartos mcoAtendTrabPartos,
			boolean flush);

	void removerMcoIntercorrenciaNascs(
			McoIntercorrenciaNascs mcoIntercorrenciaNascs, boolean flush);

	void removerMcoProfNascs(McoProfNascs mcoProfNascs, boolean flush);

	void removerMcoForcipes(McoForcipes mcoForcipes, boolean flush);

	void removerMcoCesarianas(McoCesarianas mcoCesarianas, boolean flush);

	void removerMcoNascimentos(McoNascimentos mcoNascimentos, boolean flush);

	void removerMcoReanimacaoRns(McoReanimacaoRns mcoReanimacaoRns,
			boolean flush);

	void removerMcoAchadoExameFisicos(
			McoAchadoExameFisicos mcoAchadoExameFisicos, boolean flush);

	void removerMcoExameFisicoRns(McoExameFisicoRns mcoExameFisicoRns,
			boolean flush);

	void removerMcoApgars(McoApgars mcoApgars, boolean flush);

	void removerMcoRecemNascidos(McoRecemNascidos mcoRecemNascidos,
			boolean flush);

	void removerMcoGestacoes(McoGestacoes mcoGestacoes, boolean flush);

	McoGestacoes obterMcoGestacoesPorChavePrimaria(McoGestacoesId mcoGestacoesId);

	void persistirMcoGestacaoPacientes(McoGestacaoPacientes mcoGestacaoPacientes);

	Short obterMaxSeqMcoGestacoes(Integer pacCodigo);

	void inserirMcoAnamneseEfsJn(McoAnamneseEfsJn mcoAnamneseEfsJn,
			boolean flush);

	void removerMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs);

	McoAnamneseEfs obterMcoAnamneseEfsPorChavePrimaria(
			McoAnamneseEfsId mcoAnamneseEfsId);	

	void persistirMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs);

	void persistirMcoIddGestCapurros(McoIddGestCapurros mcoIddGestCapurros);

	void persistirMcoIddGestBallards(McoIddGestBallards mcoIddGestBallards);

	void persistirMcoSnappes(McoSnappes mcoSnappes);

	void atualizarMcoApgars(McoApgars mcoApgars, boolean flush);

	void persistirMcoReanimacaoRns(McoReanimacaoRns mcoReanimacaoRns);

	void persistirMcoAchadoExameFisicos(
			McoAchadoExameFisicos mcoAchadoExameFisicos);

	void persistirMcoApgars(McoApgars mcoApgars);

	void persistirMcoRecemNascidos(McoRecemNascidos mcoRecemNascidos);

	McoForcipes obterMcoForcipesPorChavePrimaria(
			McoNascimentosId mcoNascimentosId);

	McoCesarianas obterMcoCesarianasPorChavePrimaria(
			McoNascimentosId mcoNascimentosId);

	void atualizarMcoCesarianas(McoCesarianas mcoCesarianas, boolean flush);

	void persistirMcoCesarianas(McoCesarianas mcoCesarianas);

	void persistirMcoForcipes(McoForcipes mcoForcipes);

	void persistirMcoExameFisicoRns(McoExameFisicoRns mcoExameFisicoRns);

	void persistirMcoProfNascs(McoProfNascs mcoProfNascs);

	void persistirMcoIntercorrenciaNascs(
			McoIntercorrenciaNascs mcoIntercorrenciaNascs);

	void persistirMcoNascimentos(McoNascimentos mcoNascimentos);

	void persistirMcoTrabPartos(McoTrabPartos mcoTrabPartos);

	void persistirMcoAtendTrabPartos(McoAtendTrabPartos mcoAtendTrabPartos);

	void persistirMcoMedicamentoTrabPartos(
			McoMedicamentoTrabPartos mcoMedicamentoTrabPartos);

	void persistirMcoIntercorrenciaGestacoes(
			McoIntercorrenciaGestacoes mcoIntercorrenciaGestacoes);

	void inserirMcoGestacoes(McoGestacoes mcoGestacoes, boolean flush);

	void persistirMcoResultadoExameSignifs(
			McoResultadoExameSignifs mcoResultadoExameSignifs);

	void persistirMcoBolsaRotas(McoBolsaRotas mcoBolsaRotas);

	void persistirMcoLogImpressoes(McoLogImpressoes mcoLogImpressoes);

	void persistirMcoPlanoIniciais(McoPlanoIniciais mcoPlanoIniciais);

	void desatacharMcoAnamneseEfs(McoAnamneseEfs mcoAnamneseEfs);
				
	McoEscalaLeitoRns obterMcoEscalaLeitoRnsPorLeito(String leitoID);

	List<McoPartos> listarPartosPorCodigoPaciente(Integer pacCodigo);

	void persistirMcoPartos(McoPartos mcoPartos);

	List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorConsultaObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero);

	List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorAdmissaoObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero);

	List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorNascimento(
			Integer gsoPacCodigo, Short gsoSeqp);

	List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorRnSlParto(
			Integer gsoPacCodigo, Short gsoSeqp);

	List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorExFisicoRn(
			Integer gsoPacCodigo, Short gsoSeqp);

	McoForcipes obterForcipe(Integer codigoPaciente, Short gsoSeqp, Integer seqp);
	
	List<McoResultadoExameSignifs> listarResultadosExamesSignifsPorCodigoPacienteSeqpGestacao(
			Integer codigoPaciente, Short sequence);
	
	List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoesPorCodGestCodPaciente(Short gsoSeq, Integer gsoPacCodigo,
																					DominioOcorrenciaIntercorrenciaGestacao dominioOcorrenciaIntercorrenciaGestacao);
	
	List<McoIntercorPasatus> listarIntercorPasatusPorSeq(Integer ingOpaSeq);
	
	McoIndicacaoNascimento obterMcoIndicacaoNascimentoPorChavePrimaria(Integer seq);
	
	McoAtendTrabPartos obterAtendTrabPartos(Integer codigoPaciente, Short sequence);
	
	McoIntercorPasatus obterMcoIntercorPasatusPorChavePrimaria(Integer seq);
	
	VMcoExames obterVMcoExamesPorChave(String chave);
	
	McoNotaAdicional obterMcoNotaAdicional(Integer pacCodigo, Short gsoSeqp, Byte seqp, Integer conNumero, DominioEventoNotaAdicional evento);
	
	McoProcReanimacao obterMcoProcReanimacaoPorId(Integer seq);
	List<McoNascIndicacoes> listarNascIndicacoesPorCesariana(Integer cesPacCod, Integer cesSeq, Short cesGsoSeqp);
	
	McoAnamneseEfs obterAnamnesePorGestacaoEConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero);

	McoAtendTrabPartos obterAtendTrabPartosMaxSeqp(Integer pacCodigo,Short gsoSeqp);

	McoIndicacaoNascimento obterIndicacaoNascimentoPorChavePrimaria(Integer inaSeq);

	List<McoIntercorrenciaNascs> listarIntercorrenciaNascPorCodSequenceSeq(
			Integer gsoPacCodigo, Short gsoSeqp, Integer seqp);

	List<McoNascIndicacoes> pesquisarNascIndicacoesPorForcipes(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp);
		
	McoCesarianas obterCesarianaPorChavePrimaria(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp);

	McoIntercorrencia obterIntercorrenciaoPorChavePrimaria(Short icrSeq);

	McoProcedimentoObstetrico obterProcedimentoObstetricoPorChavePrimaria(Short obsSeq);	

	McoBolsaRotas obterMcoBolsaRotasProId(McoGestacoesId id);

	List<McoPlanoIniciais> listarPlanosIniciaisPorPacienteSequenceNumeroConsulta(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero);

	McoConduta obterMcoCondutaPorChavePrimaria(Long cdtSeq);

	List<McoProfNascs> listarProfNascsPorNascimento(Integer pacCodigo,Short gsoSeqp, Integer nasSeqp);

	List<McoNotaAdicional> pesquisarNotaAdicionalPorPacienteGestacaoEvento(Integer pacCodigo, Short gsoSeqp);

	List<McoNascimentos> pesquisarNascimentosPorGestacao(Integer pacCodigo,	Short gsoSeqp);

	McoAnamneseEfs obterAnamnesePorPacienteSequenceGestacaoConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero);
	
	List<McoNotaAdicional> pesquisarNotaAdicionalPorPacienteGestacaoConsulta(Integer pacCodigo, Short gsoSeqp, Integer conNumero);
	
	Date obterMaxCriadoEmMcoGestacoes(Integer pacCodigo, Byte gesta, Date criadoEm);
	
	public List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartos(
			Integer codigoPaciente, Short sequence, 
			McoMedicamentoTrabPartos.Fields atributoNotNull,
			McoMedicamentoTrabPartos.Fields order, Boolean asc,
			Integer ... codsMdtos);

	List<McoAtendTrabPartos> listarAtendTrabPartos(Integer pacCodigo,
			Short gsoSeqp, Fields dthrAtend, Boolean indAnalgesiaBpd, Boolean indAnalgesiaBsd);

	List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(
			Integer pacCodigo, Short seqp);

	McoAnamneseEfs obterAnamnesePorPacienteGsoSeqpConNumero(Integer pacCodigo,
			Short gsoSeqp, Integer conNumero);

	McoRecemNascidos obterRecemNascidoPorPacCodigoGsoSeqpSeqp(
			Integer pacCodigo, Short gsoSeqp, Byte seqp);

	McoExameFisicoRns obterMcoExameFisicoRnsPorChavePrimaria(Integer pacCodigo,
			Short gsoSeqp, Byte seqp);

	McoSindrome obterMcoSindromePorChavePrimaria(Integer seq);

	List<McoAchadoExameFisicos> listarAchadosExamesFisicosPorCodigoPacienteGsoSeqpSepq(
			Integer pacCodigo, Short gsoSeqp, Byte seqp);

	McoAchado obterMcoAchadoObterPorChavePrimaria(Integer acdSeq);

	McoNotaAdicional pesquisarNotaAdicionalPorPacienteGestacaoSeqp(
			Integer pacCodigo, Short gsoSeqp, Byte seqp);
			
	List<McoRecemNascidos> pesquisarMcoRecemNascidoByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia);

	List<String> obterDescricaoMcoIndicacaoNascimentoByFichaAnestesia(
			Integer gsoPacCodigo, Short gsoSeqp);

	List<InformacoesPerinataisVO> pesquisarInformacoesPerinataisCodigoPaciente(Integer pacCodigo);
	
	McoAnamneseEfs obterAnamnesePorPaciente(Integer pacCodigo, Short gsoSeqp);

	boolean verificarExisteGestacao(Integer pacCodigo);

	boolean verificarExisteAtendimentoGestacaoUnidadeFuncional(Integer atdSeq,Short unfSeq );
	 
	boolean verificarExisteRecemNascido(Integer pacCodigo);

	List<McoNotaAdicional> pesquisarMcoNotaAdicional(Integer pacCodigo,
			Short gsoSeqp, Integer conNumero,
			DominioEventoNotaAdicional evento,
			McoNotaAdicional.Fields... orders);


	McoNascimentos obterMcoNascimento(Integer gsoPacCodigo, Short gsoSeqp, Date dthrInternacao, Date dataInternacaoAdministrativa);


	Boolean isSnappesPreenchido(Integer pacCodigo, Date inicioAtendimento);

	Boolean isExameFisicoRealizado(Integer pacCodigo);

	Boolean isImpressaoLogNecessaria(Integer pacCodigo);

	Boolean isImpressaoRelatorioFisicoNecessaria(Integer pacCodigo);
	
    McoGestacoes obterUltimaGestacaoGE(Integer pacCodigo);

    List<McoSnappes> listarSnappesPorCodigoPacienteDthrInternacao(Integer pacCodigo,Date dthrInternacao) ;

    void atualizarMcoSnappes(final McoSnappes mcoSnappes, final boolean flush) ;

	List<RelatorioSnappeVO> listarRelatorioSnappe2(Integer pacCodigoRecemNascido, Short seqp, Integer gsoPacCodigo, Short gsoSeqp, Byte recemNascidoSeqp) throws ApplicationBusinessException;

}
