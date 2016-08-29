package br.gov.mec.aghu.paciente.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.compras.contaspagar.vo.ListaPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.AtendimentoPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioOcorrenciaPOL;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoEnvioProntuario;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.farmacia.vo.PacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.internacao.vo.ProntuarioCirurgiaVO;
import br.gov.mec.aghu.internacao.vo.ProntuarioInternacaoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipAvisoAgendamentoCirurgia;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipControleEscalaCirurgia;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipEndPacientesHist;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemas;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipLocalizaPaciente;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipPacientesHistJn;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipProntuariosImpressos;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAipPacientesExcluidos;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistoricoPacientePolVO;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuariosVO;
import br.gov.mec.aghu.paciente.vo.AtualizarLocalAtendimentoVO;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.paciente.vo.DesarquivamentoProntuarioVO;
import br.gov.mec.aghu.paciente.vo.InclusaoAtendimentoVO;
import br.gov.mec.aghu.paciente.vo.PacienteZplVo;
import br.gov.mec.aghu.paciente.vo.ReImpressaoEtiquetasVO;
import br.gov.mec.aghu.paciente.vo.RelatorioMovimentacaoVO;
import br.gov.mec.aghu.paciente.vo.RelatorioPacienteVO;
import br.gov.mec.aghu.paciente.vo.VAipSolicitantesVO;
import br.gov.mec.aghu.paciente.vo.ZplVo;
import br.gov.mec.aghu.prescricaomedica.vo.AtendimentoJustificativaUsoMedicamentoVO;
import br.gov.mec.aghu.service.ServiceException;
import br.gov.mec.aghu.vo.ProcedimentoReanimacaoVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

@Local
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface IPacienteFacade extends Serializable {
	
	void agendarPassivarProntuario(String secao, Date dthr, boolean incluiPassivos, RapServidores servidorLogado) throws ApplicationBusinessException;

	AipPacientes obterPacientePorProntuario(Integer nroProntuario);

	AipNacionalidades obterNacionalidade(Integer codigoNacionalidade);

	List<AghAtendimentos> obterAtendimentoPorSeqHospitalDia(Integer seqHospitalDia);

	List<AghAtendimentos> obterAtendimentoPorSeqInternacao(Integer seqInternacao);

	List<AghAtendimentos> obterAtendimentoPorSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia);

	void atualizarPacPediatrico(Integer seq, Boolean indPacPediatrico, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	void atualizarAtendimento(AghAtendimentos atendimento, AghAtendimentos atendimentoOld, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException;

	Date obterMaxDataCriadoEmPesoPaciente(Integer codigoPaciente);

	Integer verificarAtendimentoPaciente(Integer altanAtdSeq, Integer altanApaSeq, MpmAltaSumario altaSumario) throws ApplicationBusinessException;

	Integer recuperarCodigoPaciente(Integer altanAtdSeq) throws ApplicationBusinessException;

	/**
	 * Obtem uma lista de atendimentos de mães com atendimento em andamento (S)
	 * e prontuário específico
	 * 
	 * @param prontuario
	 * @return lista de atendimentos de mães
	 */
	List<AghAtendimentos> obterAtendimentoPorProntuarioPacienteAtendimento(Integer prontuario);

	/**
	 * Obtem uma lista de atendimentos de recém-nascidos de um atendimento de
	 * mãe específico
	 * 
	 * @param prontuario
	 * @return lista de atendimentos de recém-nascidos
	 */
	List<AghAtendimentos> obterAtendimentosRecemNascidosPorProntuarioMae(AghAtendimentos aghAtendimentos);

	ConvenioExamesLaudosVO buscarConvenioExamesLaudos(Integer seqAtendimentoParam);

	AipPacientes obterPacientePorCodigo(Integer aipPacientesCodigo);

	AipPacientes obterPaciente(Integer aipPacientesCodigo);

	AipPesoPacientes obterPesoPacienteAtual(AipPacientes aipPaciente);

	List<AipPacientes> pesquisarPacientesPorProntuarioOuCodigo(String strPesquisa);

	List<AipPacientes> obterPacientesPorProntuarioOuCodigo(String strPesquisa);

	void atualizarPacienteAtendimento(AinAtendimentosUrgencia atendimentoUrgencia, AinInternacao internacao, AhdHospitaisDia hospitalDia, AacConsultas consulta, AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario)
			throws BaseException;

	Boolean verificarAcaoQualificacaoMatricula(String descricao) throws ApplicationBusinessException;

	AipPacientes obterAipPacientesPorChavePrimaria(Integer chavePrimaria);

	AipPacientes atualizarAipPacientes(AipPacientes aipPacientes, boolean flush);

	/**
	 * Obtem o nome de um paciente atraves de seu prontuario.
	 * 
	 * @param nroProntuario
	 * @return
	 */
	String obterNomePacientePorProntuario(Integer nroProntuario);

	AipPacienteDadoClinicos obterAipPacienteDadoClinicosPorChavePrimaria(Integer chavePrimaria);

	AipPesoPacientes buscarPrimeiroPesosPacienteOrdenadoCriadoEm(Integer pacCodigo);

	InclusaoAtendimentoVO incluirAtendimento(Integer codigoPaciente, Date dataHoraInicio, Integer seqHospitalDia, Integer seqInternacao, Integer seqAtendimentoUrgencia, Date dataHoraFim, String leitoId,
			Short numeroQuarto, Short seqUnidadeFuncional, Short seqEspecialidade, RapServidores servidor, Integer codigoClinica, RapServidores digitador,
			Integer dcaBolNumero, Short dcaBolBsaCodigo, Date dcaBolData, Integer apeSeq, Integer numeroConsulta, Integer seqGradeAgendamenConsultas, String nomeMicrocomputador) throws ApplicationBusinessException;

	String buscaUfPaciente(Integer pacCodigo);

	/**
	 * @param paciente
	 * @return
	 * @see br.gov.mec.aghu.paciente.business.ConveniosSaudePacienteON#pesquisarConveniosPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	List<AipConveniosSaudePaciente> pesquisarConveniosPaciente(AipPacientes paciente);

	void gerarMovimentacaoProntuario(Integer conNumero, RapServidores servidorLogado)	throws ApplicationBusinessException;

	List<AipSolicitantesProntuario> pesquisarSolicitantesProntuarioPorUnidadeFuncional(Short unfSeq);

	AipPacientes obterPacienteComAtendimentoPorProntuario(Integer nroProntuario);

	List<AipPacientes> obterPacienteComAtendimentoPorProntuarioOUCodigo(Integer nroProntuario, Integer codigoPaciente, List<DominioOrigemAtendimento> origemAtendimentos);

	/**
	 * Obtem o paciente e o endereço padrão através de seu prontuario.
	 * 
	 * @param aipPacientesCodigo
	 * @return
	 */
	RelatorioPacienteVO obterPacienteComEnderecoPadrao(Integer nroProntuario, Long cpf, Integer nroCodigo) throws ApplicationBusinessException;

	void validaProntuarioScheduler(Date date, String cron) throws ApplicationBusinessException;
	
	Boolean verEnvioPront(Short unfSeq, Integer pacCod, Short espSeq, Short grdSeq, Short vUnf17, Short vUnf19, Short vUnfCaps) throws ApplicationBusinessException;

	void atualizaEtiquetasImpressas(List<PacienteZplVo> pacZplVos, Boolean isReprint, Date dtReferencia, Map<Integer, PacConsultaVo> hashProntPacConsultaVo, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	Map<Integer, PacConsultaVo> obterPacConsultas(Date dtReferencia, Boolean isReprint) throws ApplicationBusinessException;

	Map<Integer, AipProntuariosImpressos> obterProntImpressos(Date dtReferencia);
	
	ZplVo obterDadosEtiquetas(Date dtReferencia, Boolean isReprint, Integer turnoDe, Integer turnoAte, String zonaDe, String zonaAte, Integer salaDe, Integer salaAte,
			Map<Integer, PacConsultaVo> hashProntPacConsultaVo, Map<Integer, AipProntuariosImpressos> hashProntImpressos) throws ApplicationBusinessException;
	
	String gerarZpl(Integer prontuario, Short volume, String nome);

	List<ReImpressaoEtiquetasVO> pesquisa(List<Integer> prontuarios, Date dataInicial, Date dataFinal);

	void validaDatas(Date dtInicial, Date dtFinal) throws ApplicationBusinessException;
	
	void atualizaVolume(Integer codPaciente, Short volume);

	void validaDadosRelProntuarioIdent(Date dtInicial, Date dtFinal) throws ApplicationBusinessException;

	List<Object> obterProntuariosIdentificados(Date dtInicial, Date dtFinal, Integer codigoAreaConsiderar, Integer codigoAreaDesconsiderar, boolean infAdicionais);
	
	void validaRemocaoMovimentacaoProntuario( Integer codigoSolicitacaoProntuario, Integer codigoPaciente) throws ApplicationBusinessException;
	
	List<VAipSolicitantesVO> obterViewAipSolicitantes(Short seq);
	
	List<RelatorioMovimentacaoVO> obterMovimentacoes(Date dtInicial, Date dtFinal, DominioSituacaoMovimentoProntuario csSituacao, Boolean csExibirArea, VAipSolicitantesVO vAipSolicitantes, Map<Short, VAipSolicitantesVO> hashSeqVAipSolicitantes);
	
	List<VAipSolicitantesVO> pesquisarAreaSolicitante(List<VAipSolicitantesVO> lista, String filtro);

	List<VAipPacientesExcluidos> pesquisaPacientesExcluidos(Date dataInicial, Date dataFinal);
	
	List<AipPacientesHist> pesquisaPacientesHistorico(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, String nome) throws ApplicationBusinessException;

	Long pesquisaPacientesHistoricoCount(String nome) throws ApplicationBusinessException;

	Long pesquisarPorFonemasCount(String nome, String nomeMae, Boolean respeitarOrdem, Date dtNascimento, DominioListaOrigensAtendimentos listaOrigensAtendimentos) throws ApplicationBusinessException;

	Long pesquisarPacienteCount(Integer nroProntuario, Integer nroCodigo, Long cpf,  BigInteger nroCartaoSaude) throws ApplicationBusinessException;

	List<AipPacientes> pesquisarPorFonemas(Integer firstResult,
			Integer maxResults, String nome, String nomeMae,
			boolean respeitarOrdem, Date dataNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException;

	public AipPacientes obterPacientePorCodigoOuProntuario(
			Integer nroProntuario, Integer nroCodigo, Long cpf,
			BigInteger... nroCartaoSaude) throws ApplicationBusinessException;

	List<AipPacientes> obterPacientePorCodigoOuProntuarioFamiliar(Integer nroProntuario,
			Integer nroCodigo, Integer nroProntuarioFamiliar) throws ApplicationBusinessException;
	
	AipPacientes pesquisarPacientePorProntuario(Integer nroProntuario);

	List<AipPacientes> pesquisarPacientesPorListaProntuario(
			Collection<Integer> nroProntuarios) throws ApplicationBusinessException;

	//.
	//List<MamDiagnostico> pesquisarDiagnosticosPorPaciente(AipPacientes paciente);

	void validaDadosPesquisaPacientes(Integer numeroProntuario,
			Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim,
			Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica,
			AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException;

	Long pesquisaPacientesCount(Integer numeroProntuario,
			Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim,
			Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica,
			AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException;

	List<AipPacientes> pesquisaPacientes(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer numeroProntuario, Date periodoAltaInicio,
			Date periodoAltaFim, Date periodoConsultaInicio,
			Date periodoConsultaFim, Date periodoCirurgiaInicio,
			Date periodoCirurgiaFim, String nomePaciente,
			AghEquipes equipeMedica, AghEspecialidades especialidade,
			FccCentroCustos servico, AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException;
	
	Long pesquisaPacienteCount(
			Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente);
	
	public List<AipPacientes> pesquisaPacientePorCodigoOuProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente);	

	String pesquisarNomeProfissional(Integer matricula, Short vinculo);
	
	List<RapServidoresVO> obterNomeProfissionalServidores(List<RapServidoresId> servidores);


	void excluirProntuario(AipPacientes paciente, String usuarioLogado) throws ApplicationBusinessException;

	List<AipPacientes> pesquisaProntuarioPaciente(Integer firstResult,
			Integer maxResults, Integer codigo, String nome, Integer prontuario);

	Long pesquisaProntuarioPacienteCount(Integer codigo, String nome,
			Integer prontuario);

	
	void passivarProntuario(String secao, Date dthr,
			boolean incluiPassivos, Date ultimaExecucaoPassivarProntuario) throws ApplicationBusinessException;

	List<AipPacientes> obterPacientesParaPassivarProntuarioScrollableResults(
			String secao, Calendar cDthr, Integer prontuario,
			boolean incluiPassivos) throws ApplicationBusinessException;

	AipPacientes obterPacientePorCodigoEProntuario(Integer nroProntuario,
			Integer nroCodigo, Long cpf) throws ApplicationBusinessException;

	void substituirProntuario(Integer prontuarioOrigem,
			Integer prontuarioDestino, Date dtIdentificacaoOrigem,
			Date dtIdentificacaoDestino, Integer codigoOrigem,
			Integer codigoDestino, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException;

	List<AipPacientes> pesquisarSituacaoProntuario(Integer firstResult,
			Integer maxResults, Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip,
			DominioSimNao indPacProtegido, Boolean consideraDigito);

	Long pesquisarSituacaoProntuarioCount(Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip,
			DominioSimNao indPacProtegido, Boolean consideraDigito);



	AipPacientes obterPacientePorCodigoOuProntuarioSemDigito(
			Integer nroProntuario, Integer nroCodigo)
			throws ApplicationBusinessException;
	
	Object[] obterPacienteHistAnterior(Integer codigo);

	void inserirPacienteHist(AipPacientesHist pacienteHist, boolean flush);

	void removerPacienteHist(AipPacientesHist pacienteHist, boolean flush);

	void atualizarPacienteHist(AipPacientesHist pacienteHist, boolean flush);

	void inserirAipPacientesHistJn(AipPacientesHistJn aipPacientesHistJn,
			boolean flush);

	List<AipEndPacientesHist> pesquisarHistoricoEnderecoPaciente(Integer codigo);

	void inserirEnderecoPacienteHist(AipEndPacientesHist endPacienteHist,
			boolean flush);

	void removerEnderecoPacienteHist(AipEndPacientesHist endPacienteHist,
			boolean flush);

	List<AipSolicitacaoProntuarios> pesquisarSolicitacaoProntuario(
			Integer firstResult, Integer maxResults, String solicitante,
			String responsavel, String observacao);

	Long pesquisarSolicitacaoProntuarioCount(String solicitante,
			String responsavel, String observacao);

	AipSolicitacaoProntuarios obterSolicitacaoProntuario(Integer codigo);

	List<AipPacientes> pesquisarPacientesPorProntuario(String strPesquisa);

	List<AipPacientes> pesquisarPacientesComProntuarioPorProntuarioOuCodigo(
			String strPesquisa);

	List<DesarquivamentoProntuarioVO> pesquisaDesarquivamentoProntuario(
			Integer slpCodigo, Date dataMovimento);

	
	List<AipMovimentacaoProntuarios> pesquisarDesarquivamentoProntuarios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	Long obterCountPesquisaDesarquivamentoProntuarios();

	
	List<ProntuarioInternacaoVO> pesquisarAvisoInternacaoSAMES();
	Long pesquisarAvisoInternacaoSAMESCount();
	
	void buscarProntuariosInternacaoDesarquivamentoProntuario(
			List<ProntuarioInternacaoVO> listaProntuariosSelecionados, String nomeMicrocomputador, final Date dataFimVinculoServidor);

	
	AipSolicitacaoProntuarios buscarProntuariosDesarquivamentoProntuario(
			List<AipMovimentacaoProntuarios> movimentacoes)
			throws ApplicationBusinessException;

	Integer gerarAtendimentoPaciente(Integer atdSeq)
			throws ApplicationBusinessException;

	void gerarJournalFichaApache(DominioOperacoesJournal operacaoJournal,
			MpmFichaApache fichaApache, MpmFichaApache fichaApacheOld)
			throws ApplicationBusinessException;

	
	void atualizarProntuarioNoAtendimento(Integer codigoPaciente,
			Integer prontuario, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException;

	List<AipFonemaPacientes> pesquisarFonemasPaciente(Integer codigoPaciente);

	AipFonemas obterAipFonemaPorFonema(String fonema);

	AipFonemaPacientes obterAipFonemaPaciente(Integer codigoPaciente,
			AipFonemas aipFonema);

	void removerAipFonemaPaciente(AipFonemaPacientes aipFonemaPaciente);

	void removerAipFonema(AipFonemas aipFonema);

	void persistirAipFonema(AipFonemas aipFonema);

	List<AipFonemasMaePaciente> pesquisarFonemasMaePaciente(
			Integer codigoPaciente);

	AipFonemasMaePaciente obterAipFonemaMaePaciente(Integer codigoPaciente,
			AipFonemas aipFonema);

	void removerAipFonemaMaePaciente(AipFonemasMaePaciente aipFonemaMaePaciente);

	List<String> obterFonemasNaOrdem(String nome) throws ApplicationBusinessException;

	Date obterDataNascimentoRecemNascidos(Integer codigoPaciente);

	McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente);

	void persistirRecemNascido(McoRecemNascidos recemNascido);

	
	String verificarLiberacaoProntuario(Integer prontuario) throws ApplicationBusinessException;

	/**
	 * Método para retornar Unidades Funcionais cadastradas em que a
	 * característica da unidade seja igual a Unid Internacao ou Unid Emergencia
	 * e que o parametro passado seja igual ao id da unidade funcional ou seja
	 * encontrado na string formada pelo andar + ala + descricao da unidade.
	 * 
	 * @return Lista com Unidades Funcionais
	 */
	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas);

	/**
	 * Método para retornar Unidades Funcionais cadastradas que possuam unidades
	 * funcionais, que a característica da unidade seja igual a Unid Internacao
	 * ou Unid Emergencia e que o parametro passado seja igual ao id da unidade
	 * funcional ou seja encontrado na string formada pelo andar + ala +
	 * descricao da unidade.
	 * 
	 * @return Lista com Unidades Funcionais
	 */
	List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas);

	List<AipPacientes> pesquisaPacientesPorSexoDataObito(
			Date dtInicialReferencia, Date dtFinalReferencia, DominioSexo sexo);

	Short obterSeqEnderecoPadraoPaciente(Integer pacCodigo);

	Short obterSeqEnderecoResidencialPaciente(Integer pacCodigo);

	String pesquisarCidadeCursor1(Integer pacCodigo, Short seqp);

	String pesquisarCidadeCursor2(Integer pacCodigo, Short seqp);

	String pesquisarCidadeCursor3(Integer pacCodigo, Short seqp);
	
	Long obterQuantidadeEnderecosPaciente(final AipPacientes pac);

	void persistirAtendimento(AghAtendimentos atendimento,
			AghAtendimentos atendimentoOld, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	AghAtendimentos clonarAtendimento(AghAtendimentos atendimento)
			throws ApplicationBusinessException;

	
	
	void reindexarPacientes() throws BaseException;

	void setTimeout(Integer timeout) throws ApplicationBusinessException;

	void sessionClear();



	void commit(Integer timeout) throws ApplicationBusinessException;

	void commit() throws ApplicationBusinessException;

	
	List<HistoricoPacientePolVO> pesquisaHistoricoPaciente(Integer prontuario)
			throws ApplicationBusinessException;

	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
			Object filtro, Object[] caracteristicas);

	List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(
			String filtro);

	void removerAtendimento(AghAtendimentos atendimentoOld, String nomeMicrocomputador)
			throws BaseException;

	void atualizarFimAtendimento(AinInternacao internacao,
			AhdHospitaisDia hospitalDia,
			AinAtendimentosUrgencia atendimentoUrgencia, Date dthrSaida, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	List<AghAtendimentos> listarAtendimentosPorConsultaEOrigem(
			DominioOrigemAtendimento dominioOrigemAtendimento,
			Integer consultaNumero);

	
	InclusaoAtendimentoVO inclusaoAtendimento(Integer codigoPaciente,
			Date dataHoraInicio, Integer seqHospitalDia, Integer seqInternacao,
			Integer seqAtendimentoUrgencia, Date dataHoraFim, String leitoId,
			Short numeroQuarto, Short seqUnidadeFuncional,
			Short seqEspecialidade, RapServidores servidor,
			Integer codigoClinica, RapServidores digitador,
			Integer dcaBolNumero, Short dcaBolBsaCodigo, Date dcaBolData,
			Integer apeSeq, Integer numeroConsulta,
			Integer seqGradeAgendamenConsultas, String nomeMicrocomputador) throws ApplicationBusinessException;

	List<AghAtendimentos> obterAtendimentoNacimentoEmAndamento(
			AipPacientes paciente);

	
	void atualizarEspecialidadeNoAtendimento(Integer seqInternacao,
			Integer seqHospitalDia, Short seqEspecialidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	
	AtualizarLocalAtendimentoVO atualizarLocalAtendimento(
			Integer seqInternacao, Integer seqHospitalDia,
			Integer seqAtendimentoUrgencia, String leitoId, Short numeroQuarto,
			Short seqUnidadeFuncional, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	
	void atualizarProfissionalResponsavelPeloAtendimento(Integer seqInternacao,
			Integer seqHospitalDia, RapServidores servidor, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	/**
	 * Pesquisa o nome do paciente buscando pelo numero do prontuario
	 * 
	 * @param prontuario
	 * @return nomePaciente
	 */
	String pesquisarNomePaciente(Integer prontuario);

	<T> Boolean modificados(T newValue, T oldValue);

	void aippSubstProntConv(Integer prontuarioOrigem, Integer prontuarioDestino)
			throws ApplicationBusinessException;

	List<PacIntdConv> pesquisarPorCodPrntDataInt(Integer codPrnt, Date dataInt);

	void removerPacIntdConv(PacIntdConv pacIntdConv, boolean flush);

	void atualizarPacIntdConv(PacIntdConv pacIntdConv, boolean flush);

	List<PacIntdConv> obterListaPacIntdConvPorAtendimento(Integer atdSeq);

	DominioSexo obterSexoPaciente(Integer pacCodigo);

	List<AipConveniosSaudePaciente> pesquisarAtivosPorPacienteCodigoSeqConvenio(
			Integer codigoPaciente, Short codigoConvenioSaudePlano,
			Byte seqConvenioSaudePlano);

	
	File refonetizarPacientes(Boolean somentePacientesSemFonemas);

	Boolean isUnidadeFuncionalBombaInfusao(Short seq);

	List<AipPacientes> pesquisarPacientePorNomeDtNacimentoComHoraNomeMae(
			String nome, Date dataNacimento, String nomeMae);

	List<AipPacientes> pesquisarPacientePorNomeENumeroCartaoSaude(String nome,
			BigInteger numCartaoSaude);

	String gerarEtiquetaPulseira(AipPacientes paciente, Integer atdSeq) throws ApplicationBusinessException;

	AipPacientes buscaPaciente(final Integer pacCodigo);

	AipPesoPacientes obterPesoPaciente(Integer codPaciente,
			DominioMomento dominioMomento);

	List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPorPacienteEDataConsulta(
			Integer pacCodigo, Date dtConsulta);
	
	List<AipCepLogradouros> listarCepLogradourosPorCEP(Integer cep);
	
	List<AipEtnia> obterTodasEtnias();

	AipEtnia obterAipEtniaPorChavePrimaria(Integer id);

	List<AipLocalizaPaciente> listarPacientesPorAtendimento(
			Integer numeroConsulta, Integer pacCodigo);

	void removerAipLocalizaPaciente(AipLocalizaPaciente aipLocalizaPaciente);

	List<AipLogProntOnlines> listarLogProntOnlines(Integer pacCodigo,
			Date dthrInicio);

	List<AipLogProntOnlines> pesquisarLogPorServidorProntuarioDatasOcorrencia(
			RapServidores servidor, Integer prontuario, Date dataInicio,
			Date dataFim, DominioOcorrenciaPOL ocorrencia, Integer firstResult,
			Integer maxResults, String orderProperty, Boolean asc);

	Long pesquisarLogPorServidorProntuarioDatasOcorrenciaCount(
			RapServidores servidor, Integer prontuario, Date dataInicio,
			Date dataFim, DominioOcorrenciaPOL ocorrencia);

	AipCidades obterCidadePorChavePrimaria(Integer chavePrimaria);

	List<AipCidades> pesquisarCidadesPorNomeSiglaUf(String nome, String siglaUf);

	AipNacionalidades obterNacionalidadePorCodigoAtiva(Integer codigo);

	Short obterValorSeqPlanoCodPaciente(Integer codigoPaciente);

	
	BigDecimal buscaMatriculaConvenio(final Integer prontuario,
			final Short convenio, final Byte plano);

	List<AipMovimentacaoProntuarios> listarMovimentacoesProntuarios(
			AipPacientes paciente, Date dthrInicio);

	List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPacienteProntRequerido(
			Integer pacCodigo, Integer numConsulta);

	void removerAipMovimentacaoProntuariosSemFlush(
			AipMovimentacaoProntuarios aipMovimentacaoProntuarios);
	
	List<Integer> pesquisarCPac(final Integer pacCodigo);
	
	List<Integer> executarCursorPaciente(Integer prontuario);

	Integer obterProntuarioPorPacCodigo(Integer pacCodigo);

	List<AipPacientes> executarCursorPac(Integer codigo);

	AipPacientes obterNomePacientePorCodigo(Integer pacCodigo);
	
	String obterNomeDoPacientePorCodigo(Integer pacCodigo);
	
	List<Integer> obtemCodPacienteComInternacaoNaoNulaEOutrasDatas(Integer pacCodigo);

	List<AipPacientes> pesquisarAipPacientesPorCodigoOuProntuario(
			final Object filtro);

	Long pesquisarAipPacientesPorCodigoOuProntuarioCount(final Object filtro);

	List<Integer> pesquisarCodigoPacientesPorNumeroQuarto(Short numeroQuarto);

	List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorEspecialidadeEPaciente(
			Short espec, Integer prontuario, List<String> situacoesIn,boolean trazHistoricos);

	List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorServidorEPaciente(
			RapServidores servidor, Integer prontuario, List<String> situacoesIn,boolean trazHistoricos);

	List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorPaciente(
			Integer prontuario, List<String> situacoesIn,boolean trazHistoricos);

	List<Object[]> pesquisaInformacoesPacienteEscalaCirurgias(Integer pacCodigo);

	List<AipPacientes> pesquisarPacientePorNumeroCartaoSaude(
			BigInteger numCartaoSaude);

	AipPacientes obterPacientePorCodigoDtUltAltaEProntuarioNotNull(
			Integer codigoPaciente, Date data);

	List<AipPacientes> recuperarPacientes(int firstResult, int maxResults,
			String orderProperty, boolean asc);

	Long recuperarPacientesCount();

	List<AipPacientes> pesquisarPacientePorNomeDtNacimentoNomeMae(String nome,
			Date dataNacimento, String nomeMae);

	AipMovimentacaoProntuarios obterMovimentacaoPorPacienteSituacaoTipoEnvioDtMovimento(
			Integer pacCodigo, DominioSituacaoMovimentoProntuario situacao,
			DominioTipoEnvioProntuario tipoEnvio, Date dataMovimento);

	AipUfs obterAipUfsPorChavePrimaria(String siglaUf);

	Boolean verificarUnidadeChecagem(Integer atdSeq);
	
	AipAlturaPacientes obterAlturaPorNumeroConsulta(Integer nroProntuario);
	
	AipPesoPacientes obterPesoPacientesPorNumeroConsulta(Integer conNumero);

	AipAlturaPacientes obterAlturasPaciente(Integer pacCodigo, DominioMomento n);

	void inserirAtendimento(AghAtendimentos atendimento,
			String nomeMicrocomputador) throws BaseException;
	
	Boolean existeEscalaDefinitiva(Short unidade, Date dataCirurgia);
	
	Date recuperarMaxDataFim(Short unidade, Date dataCirurgia);
	
	void persistirAipControleEscalaCirurgia(AipControleEscalaCirurgia aipControleEscalaCirurgia);
	
	Long countSolicitantesPorUnidadeFuncional(Short unfSeq);
		
	
	public void verificarExtensaoArquivoCSV(final String arquivo)
			throws ApplicationBusinessException;

	
	public void importarArquivo(List<String> listaLinhas, String nomeArquivo,
			String separador, Boolean anularProntuarios,
			Boolean nomeMaeNaoInformado, Boolean gerarFonemas, Boolean migrarEnderecos, Boolean migrarCodigos, Boolean tratarProntuario, String loginUsuarioLogado)
			throws ApplicationBusinessException;
	
	void atualizarMovimentacaoProntuario(AipMovimentacaoProntuarios newMovimentacaoProntuario,
			AipMovimentacaoProntuarios oldMovimentacaoProntuario);

	void limpaProntuariosLiberados();	
	
	List<AipMovimentacaoProntuarios> listarMovimentacoesProntuariosPorCodigoPacienteESituacao(Integer pacCodigo,
			Integer codEvento, Integer origemEmergencia);
	
	void persistirAvisoAgendamentoCirurgia(AipAvisoAgendamentoCirurgia avisoAgendamentoCirurgia);
	
	List<ProntuarioCirurgiaVO> pesquisarDesarquivamentoProntuariosCirurgia(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);
	
	Long obterCountPesquisaDesarquivamentoProntuariosCirurgia();
	
	void atualizarRegistrosDesarquivamentoProntuariosCirurgia(List<ProntuarioCirurgiaVO> listaProntuariosSelecionados);
	
	boolean verificarUtilizacaoDigitoVerificadorProntuario();

	AipPacientes carregarInformacoesEdicao(AipPacientes pacienteSelecionado);
	
	List<AipEtnia> pesquisarEtniaPorIDouDescricao(Object etnia);


	List<AipEnderecosPacientes> obterEnderecosPacientes(Integer pacCodigo);
	
	AipPacientes obterPacientesComUnidadeFuncional (Integer pacCodigo);

	AipPacientes obterPacientePorCodigo(Integer aipPacientesCodigo,
			Enum[] innerArgs, Enum[] leftArgs);
	
	Boolean verificarPacienteNeonatalPossuiExamePCT(Integer atdSeq,
			String exaSigla, Integer manSeq, Short ufeUnfSeq,
			String situacaoCancelado, Integer meses);


	AghAtendimentos obterAtendimento(Integer atdSeq);
	
	FatConvenioSaudePlano obterConvenioSaudePlanoAtendimento(Integer seq);


	BigDecimal obterPesoPacienteUltimaHoraPelaConsulta(Integer numeroConsulta);


	BigDecimal obterAlturaPacienteUltimaHoraPelaConsulta(Integer numeroConsulta);


	AipPacientes obterPacientePelaConsulta(Integer numeroConsulta);
	
	/**
	 * Buscar os dados da Gestante tendo como parâmetro o Prontuário
	 * 
	 * Web Service #36616
	 * 
	 * @param nroProntuario
	 * @return
	 */
	AipPacientes obterDadosGestantePorProntuario(Integer nroProntuario);
	
	/**
	 * Buscar os dados sanguíneos da gestante tendo como parâmetro o código da paciente
	 * 
	 * Web Service #36968
	 * 
	 * @param pacCodigo
	 * @param seqp
	 * @return
	 */
	AipRegSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo, Byte seqp);

	/**
	 * Verificar se existe tipo sanguíneo para o paciente
	 * 
	 * Web Service #37245
	 * 
	 * @param pacCodigo
	 * @return
	 */
	Boolean existsRegSanguineosPorCodigoPaciente(Integer pacCodigo);
	
	/**
	 * Buscar registros sem dados na tabela de AIP_REG_SANGUINEOS através do código do paciente
	 * 
	 * Web Service #36971
	 * 
	 * @param pacCodigo
	 */
	List<AipRegSanguineos> listarRegSanguineosSemDadosPorPaciente(Integer pacCodigo);

	/**
	 * Remove registro AipRegSanguineos
	 * 
	 * Web Service #36971
	 * 
	 * @param pacCodigo
	 */
	void removerAipRegSanguineos(AipRegSanguineos aipRegSanguineos);
	
	void atualizarAipRegSanguineos(AipRegSanguineos aipRegSanguineos);
	
	Byte buscaMaxSeqpAipRegSanguineos(Integer pacCodigo);
	
	void inserirAipRegSanguineos(AipRegSanguineos aipRegSanguineos);
	
	/**
	 * Verificar se existe registro sem dados na tabela de AIP_REG_SANGUINEOS através do código do paciente
	 * 
	 * Web Service #37235
	 * 
	 * @param pacCodigo
	 * @return
	 */
	Boolean existsRegSanguineosSemDadosPorPaciente(Integer pacCodigo);
	
	/**
	 * Web Service #38018
	 * 
	 * Serviço para ingressar paciente da emergência obstétrica em sala de observação
	 * 
	 * @param numeroConsulta
	 * @param codigoPaciente
	 * @throws ServiceException
	 * @throws ApplicationBusinessException
	 * 
	 * @author geancarlo.urnau
	 */
	AinAtendimentosUrgencia ingressarPacienteEmergenciaObstetricaSalaObservacao(Integer numeroConsulta, Integer codigoPaciente, String nomeMicroComputador) throws NumberFormatException, BaseException;
	
	/**
	 * Web Service #40707
	 * 
	 * Atualizar a data de nascimento com a Data de Nascimento do Recém-Nascido na tabela AIP_PACIENTES
	 * 
	 * @param pacCodigo
	 * @param dthrNascimento
	 */
	void atualizarPacienteDthrNascimento(final Integer pacCodigo, final Date dthrNascimento);
	
	/**
	 * #41035 - Listar Pacientes da CCIH
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	public List<ListaPacientesCCIHVO> pesquisarPacientesCCIH(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroListaPacienteCCIHVO filtro);

	/**
	 * Realiza a busca pelos dados originais do AghAtendimentos alterado informado.
	 * 
	 * @param aghAtendimentos - Entidade alterada
	 * @return Entidade com dados antigos
	 */
	AghAtendimentos obterAghAtendimentosOriginal (AghAtendimentos aghAtendimentos);

	public Long pesquisarPacientesCCIHCount(FiltroListaPacienteCCIHVO filtro);


	AipPacientes pesquisarPacienteComponente(Object numero, String tipo)
			throws ApplicationBusinessException;


	AipPacientesDadosCns obterDadosCNSPaciente(Integer codPaciente);

	
	
	public String obterLocalizacaoPaciente(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unf);
	/**
	 * #41796 - Obtem Registro Sanguineo do Paciente.
	 * 
	 * @param pacCodigo
	 * @return
	 */
	AipRegSanguineos obterRegSanguineoPorCodigoPaciente(Integer pacCodigo);

	void observarPersistenciaMovimentacaoProntuario(AipMovimentacaoProntuarios aipMovimentacaoProntuario, DominioOperacoesJournal dominio);
	
	/**
	 * WebService #42480
	 * @param pacCodigo
	 * @return
	 */
	BigDecimal obterPesoPacientesPorCodigoPaciente(Integer pacCodigo);

	List<ProcedimentoReanimacaoVO> listarProcReanimacao(String descricao,
			DominioSituacao situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);


	Long listarProcReanimacaoCount(String descricao, DominioSituacao situacao);


	List<AbsComponenteSanguineo> listarComponentesSuggestion(Object objPesquisa);


	Long listarComponentesSuggestionCount(Object objPesquisa);


	void persistirProcedimentoReanimacao(Integer seq, String componenteSeq,
			Integer matCodigo, String descricao, DominioSituacao situacao)
			throws ApplicationBusinessException;


	List<AfaMedicamento> listarMedicamentosSuggestion(Object objPesquisa);


	Long listarMedicamentosSuggestionCount(Object objPesquisa);


	AfaMedicamento obterMedicamentoPorId(Integer matCodigo);


	AbsComponenteSanguineo obterComponentePorId(String codigo);


	ProcedimentoReanimacaoVO obterProcReanimacao(Integer seq);


	void persistirSindrome(String descricao, String situacao);


	void ativarInativarSindrome(Integer seq);


	void persistirBallard(Short seq, Short escore, Short igSemanas);

    void atualizarAtendimentoGestante(Integer gsoPacCodigo, Short gsoSeqp, 
			String nomeMicroComputador, Integer atdSeq) throws BaseException;
	
	List<AtendimentoJustificativaUsoMedicamentoVO> obterPacientePorAtendimentoComEndereco(Integer atdSeq);

	public Character verificarPacienteImplante(Integer pacCodigo, Date dtRealizado,CirurgiaVO cirurgiaVO);
	/**
	 * #44181
	 * @author marcelo.deus
	 */
	List<AfaMedicamento> obterListaMedicamentosTubercolostaticos();
	
	
	public PacienteAgendamentoPrescribenteVO obterCodigoAtendimentoInformacoesPaciente(Integer codigo, Integer prontuario);

	MtxTransplantes pesquisarTransplantePaciente(Integer pacCodigo2, Integer seqTransplante);
    
	public List<AtendimentoPacientesCCIHVO> pesquisarPacientesBuscaAtivaCCIH(FiltroListaPacienteCCIHVO filtro);

    public Short obterSeqUnidadeFuncionalPaciente(Integer pacCodigo);
	
	public String verificarAtualizacaoPaciente(String nomePaciente, Integer numeroConsulta) throws ApplicationBusinessException;
	
	public void atualizarPacienteConsulta(AipPacientes paciente, Integer numeroConsulta);
		
	/**
	 * Verificar se existe alta para o paciente
	 * #47350
	 * 
	 * @param codPaciente Código do paciente
	 * @return Se existe alta para o paciente
	 */
	public Boolean verificarExisteAlta(Integer codPaciente);

	/***
	 * Obtém a lista de altas de um paciente
	 * #47350
	 * 
	 * @param codPaciente Código do paciente
	 * @return List de {@link MamAltaSumario} 
	 */
	public List<MamAltaSumario> obterDadosAltaPaciente(Integer codPaciente);
    
    public AipGrupoFamiliarPacientes obterDadosGrupoFamiliarPaciente(AipPacientes paciente);
    /**
     * C1#50814 e 50816
     */
	
	public List<AipPacientes> consultarDadosPacientePorNomeOuProntuarioOuCodigo(AipPacientes paciente, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	public Long consultarDadosPacientePorNomeOuProntuarioOuCodigoCount(AipPacientes paciente);
	
	void enviarEmailPacienteGMR(AghJobDetail job) throws ApplicationBusinessException;
    
    
    public List<AipCidades> obterAipCidadesPorNomeAtivo(Object filtro);
    
    public Long obterAipCidadesPorNomeAtivoCount(Object filtro);
    
    public List<AipLogradouros> obterAipLogradourosPorNome(Object filtro);
    
    public Long obterAipLogradourosPorNomeCount(Object filtro);

    public List<AipCepLogradouros> obterAipCepLogradourosPorCEP(Object filtro);
    
    public Long obterAipCepLogradourosPorCEPCount(Object filtro);
    
    public List<AipBairros> obterAipBairrosPorDescricao(Object filtro);

    public Long obterAipBairrosPorDescricaoCount(Object filtro);
    
    public List<AipUfs> obterAipUfsPorSiglaNome(Object filtro);
    
    public Long obterAipUfsPorSiglaNomeCount(Object filtro);
    
    /**
     * C1#41790
     */
    public List<AipPacientes> pesquisarPacientePorNomeOuProntuario(String strPesquisa) ;
	public Long pesquisarPacientePorNomeOuProntuarioCount(String pesquisa);
	
    
	/**
	 * Obter atendimento de paciente internado com informação do leito
	 * #50931 - C3
	 * @param nroProntuario
	 * @return
	 */
	AghAtendimentos obterAtendimentoPacienteInternadoPorProntuario(Integer nroProntuario);
	
	/**
	 * Obter lista de pacientes internados usando o leito como filtro
	 * #50931 - C2
	 * @param leitoID
	 * @return
	 */
	List<AipPacientes> obterPacienteInternadoPorLeito(String leitoId);

	public List<AipPacientes> obterPacientePorCartaoCpfCodigoPronturario(Integer nroProntuario,
			Integer nroCodigo, Long cpf, BigInteger... nroCartaoSaude) throws ApplicationBusinessException;

	List<AipPacientes> pesquisarPorFonemas(
			PesquisaFoneticaPaciente parametrosPequisa)
			throws ApplicationBusinessException;

	Long pesquisarPorFonemasCount(PesquisaFoneticaPaciente paramPesquisa)
			throws ApplicationBusinessException;

	AipPacientes obterPacientePorProntuarioLeito(Integer nroProntuario);

	List<AipSolicitantesProntuario> pesquisarUnidadesSolicitantesPorCodigoOuSigla(String pesquisa);

	void persistirAipMovimentacaoProntuario(AipMovimentacaoProntuarios aipMovimentacaoProntuarios);

	List<AipSolicitantesProntuario> verificaLocalParaMovimentacao(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados, String param) throws ApplicationBusinessException;

	void persistirDevolucaoDeProntuario(List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			RapServidores servidorLogado) throws ApplicationBusinessException;

	void persistirMovimentacaoParaUnidadeSolicitante(List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			AghUnidadesFuncionais unidadeSolicitanteAlteracao, RapServidores servidorLogado,
			DominioSituacaoMovimentoProntuario situacao) throws ApplicationBusinessException;

	Long pesquisaMovimentacoesDeProntuariosCount(AipPacientes paciente, 
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa,
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta);

	AipMovimentacaoProntuarios refreshAipMovimentacaoProntuarios(AipMovimentacaoProntuarios aipMovimentacaoProntuarios);

	List<AipMovimentacaoProntuariosVO> pesquisaMovimentacoesDeProntuarios(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AipPacientes paciente, 
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa,
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta)
					throws ApplicationBusinessException;

	void persistirCadastroOrigemProntuario(List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			AghSamis origensProntuarios, RapServidores servidorLogado) throws ApplicationBusinessException;

	AipPacientes refreshAipPaciente(AipPacientes aipPacientes);

	List<String> carregarArquivoPacientes(String caminhoAbsolutoTxt) throws FileNotFoundException;
}
