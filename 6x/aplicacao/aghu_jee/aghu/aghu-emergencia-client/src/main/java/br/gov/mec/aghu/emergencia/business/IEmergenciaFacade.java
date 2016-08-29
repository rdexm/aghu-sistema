package br.gov.mec.aghu.emergencia.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.administracao.vo.MicroComputador;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.blococirurgico.vo.SalaCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.TipoAnestesiaVO;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.configuracao.vo.EquipeVO;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.configuracao.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.controlepaciente.vo.EcpItemControleVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteServiceVO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.dominio.DominioTipoIndicacaoNascimento;
import br.gov.mec.aghu.emergencia.vo.AgrupamentoEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.BoletimAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.CalculoCapurroVO;
import br.gov.mec.aghu.emergencia.vo.DesbloqueioConsultaCOVO;
import br.gov.mec.aghu.emergencia.vo.DescritorTrgGravidadeVO;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoFiltro;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoVO;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaUnidadeVO;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.FiltroVerificaoInclusaoAnamneseVO;
import br.gov.mec.aghu.emergencia.vo.FormularioEncExternoVO;
import br.gov.mec.aghu.emergencia.vo.IntegracaoEmergenciaAghuAGHWebVO;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.emergencia.vo.MamCapacidadeAtendVO;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAguardandoAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAtendidosVO;
import br.gov.mec.aghu.emergencia.vo.MamTriagemVO;
import br.gov.mec.aghu.emergencia.vo.MedicamentoVO;
import br.gov.mec.aghu.emergencia.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.emergencia.vo.PacienteEmAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.RegistrarExameFisicoVO;
import br.gov.mec.aghu.emergencia.vo.ServidorEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.ServidorEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.TrgGravidadeFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameSignificativoVO;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.farmacia.vo.ViaAdministracaoVO;
import br.gov.mec.aghu.internacao.vo.LeitoVO;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncional;
import br.gov.mec.aghu.model.MamCaractSitEmerg;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamEmgAgrupaEsp;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamEmgServEspCoop;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamEmgServidor;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTipoCooperacao;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendeEsp;
import br.gov.mec.aghu.model.MamUnidAtendeEspId;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsId;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartosId;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascido;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascidoId;
import br.gov.mec.aghu.model.McoExameExterno;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.model.McoProcedimentoObstetricos;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoProfNascsId;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoRecemNascidosId;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.model.McoTabBallard;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;
import br.gov.mec.aghu.paciente.vo.DadosSanguineos;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.paciente.vo.PacienteProntuarioConsulta;
import br.gov.mec.aghu.perinatologia.vo.AchadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosGestacaoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.perinatologia.vo.DataNascimentoVO;
import br.gov.mec.aghu.perinatologia.vo.EscalaLeitoRecemNascidoVO;
import br.gov.mec.aghu.perinatologia.vo.ExameResultados;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaNascsVO;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaVO;
import br.gov.mec.aghu.perinatologia.vo.MedicamentoRecemNascidoVO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoFilterVO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoVO;
import br.gov.mec.aghu.perinatologia.vo.ResultadoExameSignificativoPerinatologiaVO;
import br.gov.mec.aghu.perinatologia.vo.SnappeElaboradorVO;
import br.gov.mec.aghu.perinatologia.vo.TabAdequacaoPesoPercentilVO;
import br.gov.mec.aghu.perinatologia.vo.UnidadeExamesSignificativoPerinatologiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.service.ServiceException;

@SuppressWarnings("PMD.ExcessiveClassLength")
public interface IEmergenciaFacade extends Serializable {

	public List<UnidadeFuncional> pesquisarUnidadeFuncional(Object objPesquisa);
	
	public Long pesquisarUnidadeFuncionalCount(Object objPesquisa);

	public List<MamUnidAtendem> pesquisarUnidadesFuncionaisEmergencia(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short unidadeFuncionalSeq, String descricao, DominioSituacao indSituacao);

	public Long pesquisarUnidadesFuncionaisEmergenciaCount(Short unidadeFuncionalSeq, String descricao, DominioSituacao indSituacao);

	MamUnidAtendem pesquisarUnidadeFuncionalAtivaPorUnfSeq(Short unfSeq);

	public void inserir(MamUnidAtendem mamUnidAtendem, String hostName) throws ApplicationBusinessException;

	public void alterar(MamUnidAtendem mamUnidAtendem, String hostName) throws ApplicationBusinessException;

	public void excluir(Short unfSeq) throws ApplicationBusinessException;

	public List<Especialidade> pesquisarEspecialidade(Object objPesquisa);

	public Especialidade obterEspecialidade(Short seq);

	public List<MamUnidAtendeEsp> pesquisarUnidAtendeEspPorUnidadeAtendem(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short unfSeq);

	public Long pesquisarUnidAtendeEspPorUnidadeAtendemCount(Short unfSeq);

	public List<MamProtClassifRisco> pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricao(Object objPesquisa, Integer maxResults);

	public Long pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricaoCount(Object objPesquisa);

	public void excluirUnidAtendeEsp(MamUnidAtendeEspId id);

	public void inserirUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEsp, String hostName) throws ApplicationBusinessException;

	public void alterarUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEsp, String hostName) throws ApplicationBusinessException;

	public List<Especialidade> pesquisarEspecialidadeListaSeq(List<Short> listaEspId, Object objPesquisa);
	
	public Long pesquisarEspecialidadeListaSeqCount(List<Short> listaEspId, Object objPesquisa);
	
	Especialidade obterEspecialidadePorSeq(Short espSeq) throws ApplicationBusinessException;

	public List<Short> pesquisarEspecialidadesTriagem(Long seqTriagem);

	/**
	 * Executa a pesquisa de situações de emergência
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public List<MamSituacaoEmergencia> pesquisarSituacoesEmergencia(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Short codigo,
			String descricao, DominioSituacao indSituacao);

	/**
	 * Executa o count da pesquisa de situações de emergência
	 * 
	 * @param codigo
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public Long pesquisarSituacoesEmergenciaCount(Short codigo, String descricao, DominioSituacao indSituacao);

	/**
	 * Exclui o registro executando as regras de pre-delete
	 * 
	 * @param situacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) throws ApplicationBusinessException;

	/**
	 * Executa a pesquisa de característica da situação de emergência pelo código da situação
	 * 
	 * C2 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	public List<MamCaractSitEmerg> pesquisarCaracteristicaSituacaoEmergencia(Short codigoSit);

	/**
	 * Insere ou Atualiza um registro de MamSituacaoEmergencia
	 * 
	 * @param situacaoEmergencia
	 */
	public void persistirMamSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) throws ApplicationBusinessException;

	/**
	 * Insere um registro de MamCaractSitEmerg, executando trigger de pre-insert
	 * 
	 * RN03 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_CARACT_SIT_EMERGS. MAMT_CSI_BRI
	 * @param caracSituacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void inserirMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException;

	/**
	 * Ativa/Inativa um registro de MamCaractSitEmerg, executando trigger de pre-update
	 * 
	 * RN05 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_CARACT_SIT_EMERGS. MAMT_CSI_ARU
	 * @param caracSituacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException;

	/**
	 * Exclui um registro de MamCaractSitEmerg, executando trigger de pre-delete
	 * 
	 * RN04 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_CARACT_SIT_EMERGS. MAMT_SEG_ARD
	 * @param caracSituacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException;

	MicroComputador obterMicroComputadorPorNomeOuIPException(String hostName) throws ApplicationBusinessException;

	List<Short> pesquisarUnidadesFuncionaisTriagemRecepcaoAtivas(boolean isTriagem);

	Short pesquisarUnidadeFuncionalTriagemRecepcao(List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) throws ApplicationBusinessException;

	List<Paciente> obterPacientePorCodigoOuProntuario(PacienteFiltro filtro) throws ApplicationBusinessException;

	List<Paciente> pesquisarPorFonemas(PacienteFiltro filtro) throws ApplicationBusinessException;

	Long pesquisarPorFonemasCount(PacienteFiltro filtro) throws ApplicationBusinessException;

	public List<MamUnidAtendem> listarUnidadesFuncionais(final Object parametro, boolean isRecepcao, String orderBy, boolean somenteAtivas);

	Long listarUnidadesFuncionaisCount(final Object parametro, boolean isRecepcao, boolean somenteAtivas);

	/**
	 * Exclui o registro executando as regras de pre-delete
	 * 
	 * @param espSeq
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamEmgEspecialidades(Short espSeq) throws ApplicationBusinessException;

	/**
	 * Executa a pesquisa de especialidades da emergência pelo código da especialidade
	 * 
	 * @param seq
	 * @param indSituacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<EspecialidadeEmergenciaVO> pesquisarEspecialidadeEmergenciaVO(Short seq, DominioSituacao indSituacao) throws ApplicationBusinessException;

	/**
	 * Insere ou Atualiza um registro de MamEmgEspecialidades
	 * 
	 * @param especialidadeEmergencia
	 * @param create
	 * @throws ApplicationBusinessException
	 */
	public void persistirMamEmgEspecialidades(MamEmgEspecialidades especialidadeEmergencia, boolean create) throws ApplicationBusinessException;

	/**
	 * Executa a pesquisa de agrupamentos de especialidades da emergência pelo código da especialidade
	 * 
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AgrupamentoEspecialidadeEmergenciaVO> pesquisarAgrupamentoEspecialidadeEmergenciaVO(Short seq) throws ApplicationBusinessException;

	/**
	 * Insere um registro de MamEmgAgrupaEsp, executando trigger de pre-insert
	 * 
	 * RN04 de #12167 – Manter cadastro de especialidades
	 * 
	 * @param mamEmgAgrupaEsp
	 * @throws ApplicationBusinessException
	 */
	public void inserirMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException;

	/**
	 * Ativa/Inativa um registro de MamEmgAgrupaEsp, executando trigger de pre-update
	 * 
	 * RN04 de #12167 – Manter cadastro de especialidades
	 * 
	 * @param mamEmgAgrupaEsp
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException;

	/**
	 * Exclui um registro de MamEmgAgrupaEsp, executando trigger de pre-delete
	 * 
	 * RN06 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param mamEmgAgrupaEsp
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException;

	/**
	 * Retorna as especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults) throws ApplicationBusinessException;

	/**
	 * Retorna número de especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param) throws ApplicationBusinessException;

	/**
	 * Retorna as especialidades da emergencia ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<Especialidade> pesquisarEspecialidadesEmergenciaAtivasPorSeqNomeOuSigla(String param) throws ApplicationBusinessException;

	/**
	 * Retorna o número de especialidades da emergencia ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return número da consulta (AAC_CONSULTA)
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarEspecialidadesEmergenciaAtivasPorSeqNomeOuSiglaCount(String param) throws ApplicationBusinessException;

	public Integer gravarEncaminhamentoInterno(Short espSeq, Long seqTriagem, String hostName) throws BaseException, ServiceException;
	
	List<Integer> verificarConsultasEmergencia(Short espSeq, Long trgSeq) throws ApplicationBusinessException;

	/**
	 * Executa a pesquisa de servidores da emergência
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param serVinCodigo
	 * @param matricula
	 * @param indSituacao
	 * @param nome
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ServidorEmergenciaVO> pesquisarServidorEmergenciaVO(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short serVinCodigo, Integer matricula, String indSituacao, String nome) throws ApplicationBusinessException;

	/**
	 * Executa o count da pesquisa de servidores da emergência
	 * 
	 * @param serVinCodigo
	 * @param matricula
	 * @param indSituacao
	 * @param nome
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarServidorEmergenciaVOCount(Short serVinCodigo, Integer matricula, String indSituacao, String nome) throws ApplicationBusinessException;

	/**
	 * Exclui o registro executando as regras de pre-delete
	 * 
	 * @param eseSeq
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamEmgServidores(Integer eseSeq) throws ApplicationBusinessException;

	/**
	 * Retorna os servidores ativos de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<Servidor> pesquisarServidoresAtivosPorNomeOuVinculoMatricula(String param) throws ApplicationBusinessException;

	/**
	 * Retorna o número de servidores ativos de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarServidoresAtivosPorNomeOuVinculoMatriculaCount(String param) throws ApplicationBusinessException;

	/**
	 * Insere ou Atualiza um registro de MamEmgServidor
	 * 
	 * @param situacaoEmergencia
	 */
	public void persistirMamEmgServidor(MamEmgServidor mamEmgServidor) throws ApplicationBusinessException;

	/**
	 * Executa a pesquisa de especialidades da emergência pelo código da especialidade
	 * 
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ServidorEspecialidadeEmergenciaVO> pesquisarServidorEspecialidadeEmergenciaVO(Integer seq) throws ApplicationBusinessException;

	/**
	 * Insere um registro de MamEmgServEspecialidade, executando trigger de pre-insert
	 * 
	 * RN04 de #12167 – Manter cadastro de especialidades
	 * 
	 * @param mamEmgServEspecialidade
	 * @throws ApplicationBusinessException
	 */
	public void inserirMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException;

	/**
	 * Ativa/Inativa um registro de MamEmgServEspecialidade, executando trigger de pre-update
	 * 
	 * RN04 de #12167 – Manter cadastro de especialidades
	 * 
	 * @param mamEmgServEspecialidade
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException;

	/**
	 * Exclui o registro executando as regras de pre-delete
	 * 
	 * @param mamEmgServEspecialidade
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException;

	String gerarEtiquetaPulseira(Integer pacCodigo, Integer atdSeq) throws ApplicationBusinessException;

	public Boolean encaminharPacienteAcolhimento(Paciente paciente, MamUnidAtendem unidade, Short unfSeqMicroComputador, String hostName, String nomeResponsavel)
			throws ApplicationBusinessException;

	public List<MamEmgServEspCoop> pesquisarMamEmgServEspCoopPorMamEmgServEspecialidade(Integer eseSeq, Short eepEspSeq);

	public List<MamTipoCooperacao> pesquisarMamTipoCooperacaoAtivos(String descricao);

	public void inserirMamEmgServEspCoop(MamEmgServEspCoop mamEmgServEspCoop) throws ApplicationBusinessException;

	public void excluirMamEmgServEspCoop(MamEmgServEspCoop mamEmgServEspCoop) throws ApplicationBusinessException;

	/**
	 * Pesquisa MamProtClassifRisco por descricao e/ou indSituacao
	 * 
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public List<MamProtClassifRisco> pesquisarProtClassifRisco(String descricao, DominioSituacao indSituacao);

	/**
	 * Insere um registro de MamProtClassifRisco, executando trigger de pre-insert
	 * 
	 * RN01 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void persistirMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException;

	/**
	 * Ativa/Inativa um registro de MamProtClassifRisco, executando trigger de pos-update
	 * 
	 * RN02 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException;

	/**
	 * Excluir um registro de MamProtClassifRisco, executando trigger de pos-update
	 * 
	 * RN04 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException;

	/**
	 * Permitir/Bloquear um registro de MamProtClassifRisco, executando trigger de pos-update
	 * 
	 * RN06 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void permitirBloquearChecagemMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException;

	List<PacienteAcolhimentoVO> listarPacientesAcolhimento(Short unfSeq) throws ApplicationBusinessException;
	
	List<PacienteEmAtendimentoVO> listarPacientesEmAtendimento(Short unfSeq) throws ApplicationBusinessException;

	/**
	 * Pesquisa MamFluxograma por mamProtClassifRisco
	 * 
	 * C2 de #32285 - Manter cadastro de fluxogramas utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public List<MamFluxograma> pesquisarFluxogramaPorProtocolo(MamProtClassifRisco mamProtClassifRisco);

	/**
	 * Insere/Altera um registro de MamFluxograma
	 * 
	 * RN01 de #32285 - Manter cadastro de fluxogramas utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamFluxograma
	 * @throws ApplicationBusinessException
	 */
	public void persistirMamFluxograma(MamFluxograma mamFluxograma) throws ApplicationBusinessException;

	/**
	 * Pesquisa MamFluxograma por mamProtClassifRisco
	 * 
	 * C2 de #32286 - MANTER CADASTRO DE PERGUNTAS UTILIZADAS NOS PROTOCOLOS DE CLASSIFICAÇÃO DE RISCO
	 * 
	 * @param mamFluxograma
	 * @return
	 */
	public List<MamDescritor> pesquisarDescritorPorFluxograma(MamFluxograma mamFluxograma);

	/**
	 * Insere/Altera um registro de MamDescritor
	 * 
	 * RN01 de #32286 - MANTER CADASTRO DE PERGUNTAS UTILIZADAS NOS PROTOCOLOS DE CLASSIFICAÇÃO DE RISCO
	 * 
	 * @param mamDescritor
	 * @throws ApplicationBusinessException
	 */
	public void persistirMamDescritor(MamDescritor mamDescritor) throws ApplicationBusinessException;

	/**
	 * Busca MamGravidades ativas por código ou descricao
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<MamGravidade> pesquisarGravidadesAtivasPorCodigoDescricao(Object objPesquisa, Integer pcrSeq);

	/**
	 * Busca count de MamGravidades ativas por código ou descricao
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarGravidadesAtivasPorCodigoDescricaoCount(Object objPesquisa, Integer pcrSeq);

	/**
	 * Insere/Altera um registro de MamGravidade
	 * 
	 * RN01 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamGravidade
	 * @throws ApplicationBusinessException
	 */
	public void persistirMamGravidade(MamGravidade mamGravidade) throws ApplicationBusinessException;

	/**
	 * Sobe a ordem de um registro de MamGravidade
	 * 
	 * RN04 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamGravidade
	 * @param gravidades
	 * @throws ApplicationBusinessException
	 */
	public boolean subirOrdemMamGravidade(MamGravidade mamGravidade, List<MamGravidade> gravidades) throws ApplicationBusinessException;

	/**
	 * Descer a ordem de um registro de MamGravidade
	 * 
	 * RN05 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamGravidade
	 * @param gravidades
	 * @throws ApplicationBusinessException
	 */
	public boolean descerOrdemMamGravidade(MamGravidade mamGravidade, List<MamGravidade> gravidades) throws ApplicationBusinessException;

	/**
	 * Pesquisa MamGravidade por mamProtClassifRisco
	 * 
	 * C2 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public List<MamGravidade> pesquisarGravidadePorProtocolo(MamProtClassifRisco mamProtClassifRisco);

	/**
	 * Executa a pesquisa de possiveis origens do paciente na emergência pelo código, descrição e situacao #34957 – Manter Cadastro de Origem do Paciente
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param indSituacao
	 * @param descricao
	 * @param origem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MamOrigemPaciente> pesquisarOrigensPaciente(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Integer seq,
			DominioSituacao indSituacao, String descricao);

	/**
	 * Executa o count da pesquisa de possiveis origens do paciente na emergência pelo código, descrição e situacao #34957 – Manter Cadastro de Origem do Paciente
	 * 
	 * @param seq
	 * @param indSituacao
	 * @param descricao
	 * @param origem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarOrigensPacienteCount(Integer seq, DominioSituacao indSituacao, String descricao);

	/**
	 * Insere um registro de MamOrigemPaciente #34957 – Manter Cadastro de Origem do Paciente
	 * 
	 * @param indSituacao
	 * @param descricao
	 * @throws ApplicationBusinessException
	 */
	public void persistirOrigemPaciente(DominioSituacao indSituacao, String descricao) throws ApplicationBusinessException;

	/**
	 * Atualiza um registro de MamOrigemPaciente #34957 – Manter Cadastro de Origem do Paciente
	 * 
	 * @param indSituacao
	 * @param descricao
	 * @throws ApplicationBusinessException
	 */
	public void alterarOrigemPaciente(DominioSituacao situacao, String descricao, MamOrigemPaciente mamOrigemPaciente) throws ApplicationBusinessException;

	/**
	 * Ativa/Inativa um registro de MamOrigemPaciente #34957 – Manter Cadastro de Origem do Paciente
	 * 
	 * @param mamOrigemPaciente
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamOrigemPaciente(MamOrigemPaciente mamOrigemPaciente) throws ApplicationBusinessException;

	/**
	 * Exclui o registro MamOrigemPaciente #34957 – Manter Cadastro de Origem do Paciente
	 * 
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamOrigemPaciente(Integer seq) throws ApplicationBusinessException;

	void validarAcolhimentoPaciente(Long trgSeq, String hostName, MamUnidAtendem mamUnidAtendem) throws ApplicationBusinessException;

	List<MamOrigemPaciente> listarOrigemPaciente(Object pesquisa);

	Long listarOrigemPacienteCount(Object pesquisa);

	MamTriagemVO obterTriagemVOPorSeq(Long trgSeq, Integer pacCodigo) throws ApplicationBusinessException;

	List<PostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) throws ApplicationBusinessException;
	
	Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) throws ApplicationBusinessException;
	
	List<MamTrgGerais> listarDadosGerais(Long trgSeq, String nomeComputador);

	List<MamTrgExames> listarExames(Long trgSeq, String nomeComputador);

	List<MamTrgMedicacoes> listarMedicacoes(Long trgSeq, String nomeComputador);

	MamTrgGerais inserirTrgGeral(Long trgSeq, Integer itgSeq, String nomeComputador);

	MamTrgExames inserirTrgExame(Long trgSeq, Integer emsSeq, String nomeComputador);

	void excluirTrgGeral(MamTrgGerais mamTrgGerais) throws ApplicationBusinessException;

	void excluirTrgExame(MamTrgExames mamTrgExames) throws ApplicationBusinessException;

	void excluirTrgMedicacao(MamTrgMedicacoes mamTrgMedicacoes) throws ApplicationBusinessException;

	MamTrgMedicacoes inserirTrgMedicacao(Long trgSeq, Integer mdmSeq, String nomeComputador);

	Boolean gravarDadosAcolhimento(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal, boolean validarFluxograma, String hostName)
			throws ApplicationBusinessException;

	void transferirPacienteUnidade(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal, String hostName, Paciente paciente) throws ApplicationBusinessException;

	void naoTransferirPacienteUnidade(MamTriagemVO vo, MamTriagemVO mamTriagemVOOriginal, String hostName) throws ApplicationBusinessException;

	/**
	 * Popula as listas de itens obrigatórios à partir de um descritor
	 * 
	 * @param mamDescritor
	 *            Descritor utilizado como filtro
	 * @param itensSinaisVitais
	 *            Lista de itens de sinais vitais a ser populada
	 * @param itensExames
	 *            Lista de itens de exames a ser populada
	 * @param itensMedicamecoes
	 *            Lista de itens de medicacoes vitais a ser populada
	 * @param itensGerais
	 *            Lista de itens gerais a ser populada
	 * @throws ApplicationBusinessException
	 */
	void popularItensObrigatorios(MamDescritor mamDescritor, List<ItemObrigatorioVO> itensSinaisVitais, List<ItemObrigatorioVO> itensExames,
			List<ItemObrigatorioVO> itensMedicamecoes, List<ItemObrigatorioVO> itensGerais) throws ApplicationBusinessException;

	/**
	 * Ativa/Desativa um registro de MamObrigatoriedade
	 * 
	 * RN04 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	void ativarDesativarMamObrigatoriedade(ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException;

	/**
	 * Ativa/Desativa um registro de MamObrigatoriedade
	 * 
	 * RN04 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	void removerMamObrigatoriedade(ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException;

	/**
	 * Busca os sinais vitais para popular a suggestion
	 * 
	 * C2 de #32658
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<ItemObrigatorioVO> pesquisarSinaisVitais(String parametro) throws ApplicationBusinessException;

	/**
	 * Busca o número de sinais vitais para popular a suggestion
	 * 
	 * C2 de #32658
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Long pesquisarSinaisVitaisCount(String parametro) throws ApplicationBusinessException;

	/**
	 * Lista MamItemExame ativos por seq ou descrição
	 * 
	 * C3 de #32658
	 * 
	 * @param param
	 * @param maxResults
	 * @return
	 */
	List<MamItemExame> pesquisarMamItemExameAtivosPorSeqOuDescricao(String param, Integer maxResults);

	/**
	 * Conta MamItemExame ativos por seq ou descrição
	 * 
	 * C3 de #32658
	 * 
	 * @param param
	 * @return
	 */
	Long pesquisarMamItemExameAtivosPorSeqOuDescricaoCount(String param);
	
	/**
	 * Lista MamItemMedicacao ativos por seq ou descrição
	 * 
	 * C5 de #32658
	 * 
	 * @param param
	 * @param maxResults
	 * @return
	 */
	public List<MamItemMedicacao> pesquisarMamItemMedicacaoAtivosPorSeqOuDescricao(String param, Integer maxResults);

	/**
	 * Conta MamItemMedicacao ativos por seq ou descrição
	 * 
	 * C5 de #32658
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarMamItemMedicacaoAtivosPorSeqOuDescricaoCount(String param);
	
	/**
	 * Lista MamItemGeral ativos por seq ou descrição
	 * 
	 * C7 de #32658
	 * 
	 * @param param
	 * @param maxResults
	 * @return
	 */
	public List<MamItemGeral> pesquisarMamItemGeralAtivosPorSeqOuDescricao(String param, Integer maxResults);

	/**
	 * Conta MamItemGeral ativos por seq ou descrição
	 * 
	 * C7 de #32658
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarMamItemGeralAtivosPorSeqOuDescricaoCount(String param);
	
	/**
	 * Insere um registro de MamObrigatoriedade para Sinal Vital, executando trigger de pré-insert
	 * 
	 * RN02 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	void inserirObrigatoriedadeSinalVital(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException;

	/**
	 * Insere um registro de MamObrigatoriedade para Exame, executando trigger de pré-insert
	 * 
	 * RN08 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	void inserirObrigatoriedadeExame(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException;

	/**
	 * Insere um registro de MamObrigatoriedade para Medicacao, executando trigger de pré-insert
	 * 
	 * RN11 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	void inserirObrigatoriedadeMedicacao(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException;

	/**
	 * Insere um registro de MamObrigatoriedade para Geral, executando trigger de pré-insert
	 * 
	 * RN13 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	void inserirObrigatoriedadeGeral(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException;

	List<MamPacientesAguardandoAtendimentoVO> listarPacientesAguardandoAtendimentoEmergencia(Short unfSeq, Short espSeq) throws ApplicationBusinessException;

	List<EspecialidadeEmergenciaUnidadeVO> obterEspecialidadesEmergenciaAssociadasUnidades(Short unfSeq) throws ApplicationBusinessException;

	/**
	 * Pesquisa MamFluxograma ativos por seq ou descricao e/ou seqProtocolo
	 * 
	 * @param param
	 * @param seqProtocolo
	 * @return
	 */
	List<MamFluxograma> pesquisarFluxogramaAtivoPorProtocolo(String param, Integer seqProtocolo);

	/**
	 * Conta MamFluxograma ativos por seq ou descricao e/ou seqProtocolo
	 * 
	 * @param param
	 * @param seqProtocolo
	 * @return
	 */
	Long pesquisarFluxogramaAtivoPorProtocoloCount(String param, Integer seqProtocolo);

	/**
	 * Pesquisa MamDescritor por mamFluxograma
	 * 
	 * C2 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param mamFluxograma
	 * @param trgGravidade
	 * @return
	 */
	List<DescritorTrgGravidadeVO> pesquisarDescritorAtivoPorFluxogramaGravidadeAtivaTriagem(MamFluxograma mamFluxograma, MamTrgGravidade trgGravidade);
	
	/**
	 * Cria uma mensagem descritiva do tempo máximo em espera
	 * 
	 * @param tempo
	 * @return
	 */
	String criarMensagemTempoMaximoEspera(Date tempo);

	/**
	 * Verifica se deve desconsiderar a classificação de risco para o paciente em questão uma vez que o mesmo tenha sido transferido de unidade e já tenham feito classificação de
	 * risco
	 * 
	 * RN06 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeqAcolhimento
	 * @param unfSeqAcolhimento
	 * @return
	 */
	TrgGravidadeFluxogramaVO obterClassificacaoRiscoPaciente(Long trgSeqAcolhimento, Short unfSeqAcolhimento);
	
	/**
	 * Regras para botão Gravar (Obs.: Sempre que esse botão for clicado deve-se inserir um novo registro em MAM_TRG_GRAVIDADES.
	 * 
	 * RN02 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	void gravarMamTrgGravidade(Long trgSeq, DescritorTrgGravidadeVO item, String micNome) throws ApplicationBusinessException;
	
	List<BoletimAtendimentoVO> carregarDadosBoletim(Integer consulta)
			throws ApplicationBusinessException;
	
	public void verificaCodSituacaoEmergenciaDoEncExt(String hostName, Long trgSeq) throws ApplicationBusinessException;
	
	public void gravarEncaminhamentoExterno(Long trgSeq, String especialidade, PostoSaude unidade, String obs, String hostName, Short seqp) throws ApplicationBusinessException;

	public String realizaEncaminhamentoExternoValidandoGravidade(String observacao, PostoSaude postoSaude,String especialidade)throws ApplicationBusinessException, ServiceException;

	List<PostoSaude> pesquisarUnidadeSaudeExterno(Object objPesquisa) throws ApplicationBusinessException;

	List<FormularioEncExternoVO> carregarDadosFormulario(Long triagem,
			Short seqpTriagem, String cidadeLocal) throws ApplicationBusinessException;
	
	List<Short> pesquisarSeqsEspecialidadesEmergencia(Short codigo, DominioSituacao indSituacao);
	
	List<MamCapacidadeAtendVO> pesquisarCapacidadesAtends(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short espSeq, DominioSituacao indSituacao) throws ApplicationBusinessException;
	
	Long pesquisarCapacidadesAtendsCount(Short espSeq, DominioSituacao indSituacao);
	
	void excluirCapacidadeAtend(MamCapacidadeAtendVO capacidadeSelecionada);
	
	void persistirCapacidadeAtend(Short espSeq, Integer capacidadeSeq, Short qtdeInicialPac, Short qtdeFinalPac,
			Short capacidadeAtend, Boolean indSituacao) throws ApplicationBusinessException;

	public Short obtemUltimoSEQPDoAcolhimento(Long trgSeq);
	
	/**
	 * Obtém o identificador da triagem
	 * 
	 * C7 de #37859
	 * 
	 * @param consulta
	 * @return
	 */
	Long obterTrgSeq(Integer consulta);
	
	/***
	 * @ORADB MAMK_SITUACAO_EMERG.MAMC_GET_SIT_EMERG
	 * @param caracteristica
	 */
	List<Short> obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia caracteristica);
	
	List<Date> obterDtHrSituacaoExtratoTriagem(Long trgSeq, Short segSeq);
	
	List<EcpItemControleVO> buscarItensControlePorPacientePeriodo(Integer pacCodigo, Long trgSeq);

	List<RegistroControlePacienteServiceVO> pesquisarRegistrosPaciente(
			Integer pacCodigo, List<EcpItemControleVO> listaItensControle, Long trgSeq)
			throws ApplicationBusinessException;
	
	boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo) throws ServiceException;

	void excluirRegistroControlePaciente(Long seqHorarioControle) throws ApplicationBusinessException;

	Boolean validaUnidadeFuncionalInformatizada(Short unfSeq)
			throws ApplicationBusinessException;

	boolean exibirModalPacienteMenor(Paciente paciente, MamUnidAtendem unidade)
			throws ApplicationBusinessException;

	Boolean existeTriagem(Long trgSeq);
	
	void atualizarSituacaoTriagem(MamTriagens mamTriagem, MamTriagens mamTriagemOriginal, String hostName) throws ApplicationBusinessException;
	
	void verificarNotificacaoGmr(PacienteEmAtendimentoVO pacienteSelecionado) throws ApplicationBusinessException;
	
	public Boolean imprimirEmitirBoletimAtendimento(Short unfSeq) throws ServiceException ;

	Short obterUltimaGestacaoRegistrada(Integer pacCodigo);

	String verificaImpressaoAdmissaoObstetrica(PacienteEmAtendimentoVO pacienteSelecionado, Short seqP);

	Integer obterMatricula();
	
	Short obterVinculo();
	
	DominioTipoFormularioAlta obterTipoFormularioAlta(Long seq);

	List<MamPacientesAtendidosVO> listarPacientesAtendidos(Short unfSeq,
			Short espSeq) throws ApplicationBusinessException;

	Map<Integer, String> reabrirPacienteAtendidos(Long trgSeq, Integer atdSeq,
			Short unfSeq, String pacNome, Short espSeq, String hostName)
			throws ApplicationBusinessException;

	void desbloqueioSumarioAlta(Integer atdSeq, String nomeMicromputador)
			throws ApplicationBusinessException;

	void verificarUnfPmeInformatizada(Short seq, PacienteEmAtendimentoVO pacienteSelecionado) throws ApplicationBusinessException;

	boolean verificarUnfEmergObstetrica(Short unfSeq) throws ServiceException;

	void verificarSituacaoPacientes(List<PacienteEmAtendimentoVO> listaPacientes, Short unfSeq) throws ApplicationBusinessException, ServiceException;
	
	public IntegracaoEmergenciaAghuAGHWebVO iniciarAtendimentoPaciente(Integer atdSeq, Integer conNumero, Long trgSeq, Date dataHoraInicio, Short unfSeq, Short espSeq, Integer pacCodigo, String hostName, Boolean atenderMedicoAghWeb) throws ServiceException, BaseException;

	public Boolean verificaIntegracaoAghuAghWebEmergencia() throws ApplicationBusinessException;
	
	public Boolean verificaAgendaEmergencia(Short unfSeq) throws ServiceException;
	Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero);

	DominioSimNao obterParametroFormularioExterno() throws ApplicationBusinessException;
	MamOrigemPaciente obterOrigemPacientePorChavePrimaria(Integer seq);
	
	List<McoLogImpressoes> listarLogImpressoes(Integer codigoPaciente, Short sequence);
	
	Integer obterSeqpLogImpressoes(Integer gsoPacCodigo, Short gsoSeqp);

	void inserirLogImpressoes(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp, Integer conNumero, String evento,
			Integer matricula, Short vinCodigo, Date criadoEm);
		
	public Paciente reimpressaoPulseiraPacEmergencia(Integer codigoPaciente, MamUnidAtendem mamUnidAtendem)throws ApplicationBusinessException;
	
	public Long obterTrgSeqParaAcolhimento(Integer numeroConsulta) throws ApplicationBusinessException;
	
	void atualizaPrevisaoAtendimento(Short espSeq) throws ApplicationBusinessException;

	/**
	 * Consulta utilizada para recuperar os dados da gestação tendo o sequencial
	 * da gestação.
	 * 
	 * C5 de #36508 - Pesquisar Gestações
	 * 
	 * @param pacCodigo
	 */
	List<McoGestacoes> pesquisarMcoGestacoesPorPaciente(Integer pacCodigo);

	/**
	 * RN01 e RN03 de #36508 - Pesquisar Gestações
	 * 
	 * @param nroProntuario
	 * @param nroConsulta
	 * @throws ApplicationBusinessException
	 */
	PacienteProntuarioConsulta obterDadosGestante(Integer nroProntuario,
			Integer nroConsulta) throws ApplicationBusinessException;

	void cadastrarProcedimento(McoProcedimentoObstetricos procedimento)
			throws ApplicationBusinessException;

	void ativarDesativarProcedimentoObstetrico(
			McoProcedimentoObstetricos procedimentoObstetrico)
			throws ApplicationBusinessException;

	McoGestacoes obterMcoGestacoes(final McoGestacoesId id);

	List<McoProcedimentoObstetricos> pesquisarMcoProcedimentoObstetricos(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short seq, String descricao, Integer codigoPHI,
			DominioSituacao dominioSituacao);

	McoProcedimentoObstetricos buscarMcoProcedimentoObstetricosPorSeq(Short seq);

	void alterarProcedimento(McoProcedimentoObstetricos procedimento)
			throws ApplicationBusinessException;

	/**
	 * RN02 de #36508 - Pesquisar Gestações
	 * 
	 * Buscar a idade do paciente através da Data de Nascimento
	 * 
	 * @param dtNasc
	 */
	String getIdadeFormatada(Date dtNasc);

	/**
	 * RN05 de #36508 - Pesquisar Gestações
	 * 
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	Integer buscarUltimaConsultaCO(Integer pacCodigo)
			throws ApplicationBusinessException;

	/**
	 * Estória #26116
	 */
	List<McoIndicacaoNascimento> pesquisarIndicacoesNascimento(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao,
			DominioTipoIndicacaoNascimento tipoIndicacao,
			DominioSituacao situacao);

	Long pesquisarIndicacoesNascimentoCount(Integer codigo, String descricao,
			DominioTipoIndicacaoNascimento tipoIndicacao,
			DominioSituacao situacao);

	void persistirIndicacaoNascimento(
			McoIndicacaoNascimento indicacaoNascimento,
			McoIndicacaoNascimento indicacaoNascimentoOriginal)
			throws BaseException;

	/**
	 * Estória #26112
	 */
	List<McoExameExterno> pesquisarExamesExternos(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, String nome,
			DominioSituacao situacao);

	Long pesquisarExamesExternosCount(String nome, DominioSituacao situacao);

	McoExameExterno obterMcoExameExterno(Integer seq);

	void persistirExameExterno(McoExameExterno exameExterno)
			throws ApplicationBusinessException;

	void ativarInativarExameExterno(McoExameExterno exameExterno)
			throws ApplicationBusinessException;

	void excluirExameExterno(McoExameExterno exameExterno)
			throws ApplicationBusinessException;

	/**
	 * #32283 - Obter Diagnostico
	 * 
	 * @param seq
	 * @return
	 */
	McoDiagnostico obterMcoDiagnostico(Integer seq);

	/**
	 * Ativa/Inativa um registro de McoDiagnostico
	 * 
	 * #26109 - RN06
	 * 
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	void ativarInativarDiagnostico(McoDiagnostico diagnostico)
			throws ApplicationBusinessException;

	Long obterListaDiagnosticoCount(DiagnosticoFiltro filtro);

	/**
	 * Pesquisa diagnostico
	 * 
	 * C1 #26109 - Consulta Diagnostico utilizando filtros da tela.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	List<DiagnosticoVO> pesquisarDiagnosticos(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			DiagnosticoFiltro filtro) throws ApplicationBusinessException;

	CidVO obterCidVO(Integer seq) throws ApplicationBusinessException;

	/**
	 * #32283 - Gravar/Atualizar Diagnostico
	 * 
	 * @param seq
	 * @return
	 */

	void persistirDiagnostico(McoDiagnostico diagnostico)
			throws ApplicationBusinessException;

	/**
	 * #32283 - Obter lista de cids
	 * 
	 * @param param
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<CidVO> obterCids(String param) throws ApplicationBusinessException;

	Long obterCidsCount(String param) throws ApplicationBusinessException;

	DadosGestacaoVO pesquisarMcoGestacaoPorId(Integer pacCodigo, Short seqp, Integer numeroConsulta) throws ApplicationBusinessException;

	boolean temGravidezConfirmada(Integer pacCodigo, Short gsoSeqp);
	
	String preencherInformacoesPaciente(String nomePaciente,
			String idadeFormatada, Integer prontuario, Integer numeroConsulta);

	DadosSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo,
			Byte seqp) throws ApplicationBusinessException;

	Long pesquisarMcoProcedimentoObstetricosCount(Short seq, String descricao,
			Integer codigoPHI, DominioSituacao dominioSituacao);

	/**
	 * Busca ultima consulta da gestação
	 * 
	 * RN04 de #36508 - Pesquisar Gestações
	 * 
	 * @param gsoSeqp
	 * @param pacCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer buscarUltimaConsulta(Short gsoSeqp, Integer pacCodigo)
			throws ApplicationBusinessException;

	void validarParto(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo,
			Short seqp) throws ApplicationBusinessException;

	void validarEcoSemanasDias(DadosGestacaoVO dadosGestacaoVO,
			boolean isSemanas, boolean atualizar, boolean calcularIg)
			throws ApplicationBusinessException;

	void calcularDtProvavelParto(DadosGestacaoVO dadosGestacaoVO);

	public List<RapServidorConselhoVO> pesquisarServidoresConselho(
			String strPesquisa) throws ApplicationBusinessException;

	public Long pesquisarServidoresConselhoCount(String strPesquisa) throws ApplicationBusinessException;

	void gravar(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo, Short seqp,
			List<ExameResultados> examesResultados, List<ResultadoExameSignificativoPerinatologiaVO> resultadosExamesExcluidos)
			throws ApplicationBusinessException;

	boolean isMcoGestacoesAlterada(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal);

	boolean preGravar(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo,
			Short seqp) throws ApplicationBusinessException;

	List<McoIndicacaoNascimento> pesquisarIndicacoesNascimentoPorSeqNome(
			String objPesquisa);

	Long pesquisarIndicacoesNascimentoPorSeqNomeCount(String objPesquisa);

	List<br.gov.mec.aghu.farmacia.vo.MedicamentoVO> obterMcoMedicamentoPorSeqOuDescricao(Object objPesquisa)
			throws ServiceException;

	Long obterMcoMedicamentoPorSeqOuDescricaoCount(Object objPesquisa)
			throws ServiceException;

	List<DadosNascimentoVO> pesquisarMcoNascimentoPorId(Integer pacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException;
	
	boolean calcularDuracaoTotalParto(Integer gsoPacCodigo, Short gsoSeqp, String periodoExpulsivo, String periodoDilatacao,
			DadosNascimentoSelecionadoVO nascimentoSelecionado, DadosNascimentoVO nascimento);
	
	DadosNascimentoSelecionadoVO obterDadosNascimentoSelecionado(Integer seqp, Integer gsoPacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException;
	
	TipoAnestesiaVO obterAnestesiaAtiva(Short tanSeq) throws ApplicationBusinessException;

	Date obterDtHrNascimento(Integer pacCodigo, Short gsoSeqp);

	List<McoAtendTrabPartos> listarAtendPartosPorId(Short gsoSeqp,
			Integer gsoPacCodigo);
	 
	void gravarAtendTrabParto(McoAtendTrabPartos atendTrabParto)
			throws ApplicationBusinessException;

	McoGestacoes pesquisarMcoGestacaoPorPacCodigoSeqp(Integer pacCodigo, Short seqp);

	void excluirAtendTrabParto(McoAtendTrabPartos atendTrabParto);
	
	boolean preGravarNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimento) throws ApplicationBusinessException;
	
	/**
	 * #26113
	 */
	List<ExameSignificativoVO> pesquisarExamesSignificativos(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean cargaExame, int firstResult, int maxResults) throws ApplicationBusinessException;
		
	Long pesquisarExamesSignificativosCount(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean cargaExame) throws ApplicationBusinessException;
	
	List<ExameMaterialAnaliseVO> pesquisarExameMaterial(String descricao) throws ApplicationBusinessException;
	
	Long pesquisarExameMaterialCount(String descricao) throws ApplicationBusinessException;
	
	List<UnidadeFuncionalVO> pesquisarUnidadeFuncionalVO(String descricao) throws ApplicationBusinessException;
	
	Long pesquisarUnidadeFuncionalCount(String descricao) throws ApplicationBusinessException;

	void persitirExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq, Boolean cargaExame) throws ApplicationBusinessException;
	
	void excluirExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq) throws ApplicationBusinessException;

	McoAtendTrabPartos obterAtendTrabPartoPorId(McoAtendTrabPartosId id);

	void adicionarMedicamento(McoMedicamentoTrabPartos medicamento)throws ApplicationBusinessException;

	List<MedicamentoVO> pesquisarTrabPartoMedicamentos(Integer gsoPacCodigo, Short gsoSeqp);
	
	McoMedicamentoTrabPartos buscarMcoMedicamentoTrabPartosPorId(Integer pacCodigo, Short seqp,Integer matCodigo);

	void alterarMcoMedicamentoTrabPartos(McoMedicamentoTrabPartos medicamento) throws ApplicationBusinessException;

	void gravarBolsaRotas(McoBolsaRotas bolsaRotas)throws ApplicationBusinessException;

	void gravarTrabalhoParto(McoTrabPartos mcoTrabPartos, McoBolsaRotas bolsaRotas)throws ApplicationBusinessException;
	
	void gravarDadosAbaNascimento(Integer conNumero, String hostName, DadosNascimentoVO nascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException;
	
	DadosNascimentoVO gravarNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimentoVO,
			Short seqAnestesia) throws ApplicationBusinessException;
	
	void excluirNascimento(DadosNascimentoVO nascimentoExcluir, Integer gsoPacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException;
	
	List<TipoAnestesiaVO> pesquisarTiposAnestesiasAtivas(Object param);
	
	Long pesquisarTiposAnestesiasAtivasCount(Object param);
	
	Boolean isAnamnese(FiltroVerificaoInclusaoAnamneseVO filtroVO) throws BaseException;

	List<McoDiagnostico> pesquisarDiagnosticoSuggestion(String strPesquisa);

	Long pesquisarDiagnosticoSuggestionCount(String strPesquisa);

	/*
	 * #26108
	 */
	List<McoConduta> listarCondutas(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Long codigo, String descricao,
			Integer faturamento, DominioSituacao situacao);

	Long listarCondutasCount(Long codigo, String descricao, Integer faturamento, DominioSituacao situacao);
	
	void persistirConduta(McoConduta conduta) throws ApplicationBusinessException;
	
	void ativarInativarConduta(McoConduta conduta);
	
	/**
	 * Pesquisa McoIndicacaoNascimento ativos por descrição
	 * 
	 * C1 de 37857
	 * 
	 * @param descricao
	 * @param maxResults
	 * @return
	 */
	List<McoIndicacaoNascimento> pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeq(String descricao, Integer maxResults);

	/**
	 * Count McoIndicacaoNascimento ativos por descrição
	 * 
	 * C1 de 37857
	 * 
	 * @param descricao
	 * @param maxResults
	 * @return
	 */
	Long pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeqCount(String descricao);
	
	/**
	 * Pesquisar indicações para parto instrumentado
	 * 
	 * C2 de #37857
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	List<McoNascIndicacoes> pesquisarIndicacoesPartoInstrumentado(final Integer gsoPacCodigo, final Short gsoSeqp, final Integer seqp);

	void excluirMedicamento(Integer matCodigo, Short gsoSeqp,Integer gsoPacCodigo);
	
	/**
	 * Pesquisar indicações para parto Cesariana
	 * 
	 * C3 de #37857
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	List<McoNascIndicacoes> pesquisarIndicacoesCesariana(final Integer gsoPacCodigo, final Short gsoSeqp, final Integer seqp);

	/**
	 * Pesquisa McoIntercorrenciaNascs por nascimento
	 * 
	 * C1 de #37859
	 * 
	 * @param nasGsoPacCodigo
	 * @param nasGsoSeqp
	 * @param nasSeqp
	 * @return
	 */
	List<IntercorrenciaNascsVO> pesquisarMcoIntercorrenciaNascsPorNascimento(final Integer nasGsoPacCodigo, final Short nasGsoSeqp,
			final Integer nasSeqp) throws ApplicationBusinessException;

	/**
	 * Pesquisa de ativos por Seq Ou Descricao
	 * 
	 * C2 de #37859
	 * 
	 * @param parametro
	 * @param maxResults
	 * @return
	 */
	List<IntercorrenciaVO> pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricao(final String parametro) throws ApplicationBusinessException;

	/**
	 * Count de ativos por Seq Ou Descricao
	 * 
	 * C2 de #37859
	 * 
	 * @param parametro
	 * @return
	 */
	Long pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricaoCount(final String parametro);

	List<McoAnamneseEfs> obterListaMcoAnamneseEfs(Integer paciente, Short sequence);

	List<CidVO> pesquisarCIDSuggestion(String strPesquisa) throws ApplicationBusinessException;

	Long pesquisarCIDSuggestionCount(String strPesquisa) throws ApplicationBusinessException;

	/**
	 * RN01 de #37859
	 * 
	 * @param nasGsoPacCodigo
	 * @param nasGsoSeqp
	 * @param nasSeqp
	 * @param consulta
	 * @param dataHoraIntercorrencia
	 * @param intercorrenciaVO
	 * @param procedimentoObstetrico
	 * @return
	 * @throws ApplicationBusinessException
	 */
	IntercorrenciaNascsVO inserirMcoIntercorrenciaNascs(final Integer nasGsoPacCodigo, final Short nasGsoSeqp, final Integer nasSeqp,
			final Integer consulta, final Date dataHoraIntercorrencia, final IntercorrenciaVO intercorrenciaVO,
			final McoProcedimentoObstetricos procedimentoObstetrico) throws ApplicationBusinessException;

	/**
	 * RN04 de #37859
	 * 
	 * @param intercorrenciaNascsVO
	 * @throws ApplicationBusinessException
	 */
	void removerMcoIntercorrenciaNascs(IntercorrenciaNascsVO intercorrenciaNascsVO) throws ApplicationBusinessException;

	/**
	 * RN01 de #37859
	 * 
	 * @param intercorrenciaNascsVO
	 * @param dataHoraIntercorrencia
	 * @param intercorrenciaVO
	 * @param procedimentoObstetrico
	 * @return
	 * @throws ApplicationBusinessException
	 */
	IntercorrenciaNascsVO alterarMcoIntercorrenciaNascs(final Integer consulta, final IntercorrenciaNascsVO intercorrenciaNascsVO,
			final Date dataHoraIntercorrencia, final IntercorrenciaVO intercorrenciaVO,
			final McoProcedimentoObstetricos procedimentoObstetrico) throws ApplicationBusinessException;

	List<EquipeVO> pesquisarEquipeAtivaCO(Object param) throws ApplicationBusinessException;
	
	Long pesquisarEquipeAtivaCOCount(Object param) throws ApplicationBusinessException;
	
	EquipeVO obterEquipePorId(Short eqpSeq) throws ApplicationBusinessException;
	
	List<SalaCirurgicaVO> obterSalasCirurgicasAtivasPorUnfSeqNome(Object param) throws ApplicationBusinessException;
	
	Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(Object param) throws ApplicationBusinessException;
	
	SalaCirurgicaVO obterDadosSalaCirurgica(Short unfSeq, Short seqp) throws ApplicationBusinessException;
	
	EquipeVO buscarEquipeAssociadaLaudoAih(Integer conNumero, Integer pacCodigo) throws ApplicationBusinessException;
	
	/**
	 * RN02 - #37869
	 * Listar profisisonais
	 * @param gsoSeqp
	 * @param gsoPacCodigo
	 * @param nasSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<RapServidorConselhoVO> listarProfissionais(Short gsoSeqp, Integer gsoPacCodigo, Integer nasSeqp) throws ApplicationBusinessException;
	
	/**
	 * RN03 - #37869
	 * @param profNascs
	 */
	void gravarMcoProfNasc(McoProfNascs profNascs)throws ApplicationBusinessException;
	
	/**
	 * RN05 - #37869
	 * @param id
	 */
	void excluirMcoProfNasc(McoProfNascsId id) throws ApplicationBusinessException;
	
	/**
	 *  #34780 - Listar servidores conselho
	 * @param sigla
	 * @param centroCusto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(String sigla) throws ApplicationBusinessException;
	
	McoBolsaRotas buscarBolsaRotas(Integer pacCodigo, Short seqp);
	
	/**
	 *  #34780 - Listar servidores conselho count
	 * @param sigla
	 * @param centroCusto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(String sigla) throws ApplicationBusinessException;
	
	void excluirConduta(McoPlanoIniciais planoIniciais, McoAnamneseEfs anamneseEfs) throws BaseException;
	
	McoTrabPartos buscarMcoTrabPartos(Integer pacCodigo, Short seqp);
	
	McoAtendTrabPartos buscarMcoAtendTrabPartos(Integer pacCodigo, Short seqp);
	
	Boolean verificarSumarioPrevio(Integer pacCodigo, Short seqp);

	McoNascimentos obterMcoNascimentosPorChavePrimaria(Integer seqp, Integer codigoPaciente, Short sequence);

	List<McoConduta> pesquisarMcoCondutaSuggestion(String strPesquisa);

	Long pesquisarMcoCondutaSuggestionCount(String strPesquisa);

	void validarDataConsulta(Date dthrConsulta) throws ApplicationBusinessException;

	void persistirMcoAnamneseEfs(McoAnamneseEfs anamneseEfs, Integer conNumero, Integer pacCodigo, Short seqp) throws BaseException;

	void verificarDilatacao(String dilatacao) throws BaseException;

	/**
	 * Suggestion Exames - COUNT
	 * 
	 * #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param param
	 * @return
	 */
	Long pesquisarExamesCount(String param) throws ApplicationBusinessException;

	/**
	 * Suggestion Exames - PESQUISA
	 * 
	 * #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param param
	 * @return
	 */
	List<UnidadeExamesSignificativoPerinatologiaVO> pesquisarExames(String param) throws ApplicationBusinessException;

	McoIndicacaoNascimento obterMcoIndicacaoNascimentoPorChavePrimaria(Integer pk);

	List<McoPlanoIniciais> listarMcoPlanoIniciaisConduta(Integer numeroConsulta, Short seqp, Integer pacCodigo);
	
	McoAnamneseEfs obterMcoAnamneseEfsPorId(McoAnamneseEfsId pk);

	void insereConduta(McoConduta conduta, McoAnamneseEfs anamneseEfs, String complemento);

	void atualizaConduta(McoConduta conduta, McoAnamneseEfs anamneseEfs, String complemento);

	RapServidorConselhoVO obterRapPessoasConselhoPorMatriculaVinculo(Short vinculo,Integer matricula) throws ApplicationBusinessException;

	McoTrabPartos obterMcoTrabPartoPorId(Integer pacCodigo, Short seqp);

	McoBolsaRotas obterBolsaRotaPorId(Integer pacCodigo, Short seqp);

	void ingressarConsultaSO(Integer codigoPaciente, Integer numeroConsulta, String hostName) throws ApplicationBusinessException ;
	
	void gravarConsultaCO(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal) throws ApplicationBusinessException;
	
	void validarDadosConsultaCO(McoBolsaRotas bolsaRotas) throws ApplicationBusinessException;
	
	void validarDadosBolsaRotas(McoBolsaRotas bolsaRotas) throws ApplicationBusinessException;
	
	Long obtemQuantidadeDefetosDaGestante(Integer pacCodigo, Short seqp);

	boolean verificarAlteracaoTela(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal);

	/**
	 * RN01 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Object[] carregarExames(Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException;
	
	McoLogImpressoes verificaImpressaoAdmissaoObstetrica(Integer pacCodigo, Short seqP);

	List<CidVO> obterCidPorSeq(List<Integer> cids) throws ServiceException;
	
	void elaborarPrescricaMedica(Integer numeroConsulta, Short seqUnidadeFuncional) throws ApplicationBusinessException; 
	
	/**
	 * RN05 de 26349
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	void ajustarDesbloqueioConsultaCO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException;
	
	/**
	 * RN06 de 26349
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @return
	 */ 
	void ajustarDesbloqueioConsultaCOSelecionada(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException;
	
	/**
	 * RN01 de 26349
	 * 
	 * @param finalizarInternarConsultaCOVO
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @throws ApplicationBusinessException
	 */
	public void desbloquearConsultaCO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException;

	void validarDadosGestacional(Integer pacCodigo, Short seqp) throws ApplicationBusinessException;
	
	void finalizarConsulta(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp, Integer matricula, Short vinCodigo, Date criadoEm, String hostName, Boolean isEmergenciaCustom) throws ApplicationBusinessException;

	boolean gravarSumarioDefinitivo(Integer pacCodigo, 
			Short seqp,  
			Integer conNumero, 
			String hostName, 
			DadosNascimentoVO nascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException;

	Integer solicitarExameVdrl(Integer numeroConsulta);
	
	Object[] obterConselhoESiglaVRapServidorConselho();

	void executaImpressaoGeracaoPendenciaAssinatura(Integer pacCodigo, Short gsoSeqp);

	Boolean gerarPendenciaAssinaturaDigital()throws ServiceException, ApplicationBusinessException;
	
	void verificarIdadeGestacional(Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException;
	
	void verificaSeExisteCondutaSemComplementoNaoCadastrada(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException;
	
	boolean verificarTipoDeGravidez(Integer pacCodigo, Short seqp);
	
	void realizarInternacao(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException;
	
	boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException;
	
	RapServidorConselhoVO verificarPermissaoUsuario(Integer matricula, Short vinCodigo) throws ApplicationBusinessException, ServiceException;
	
	boolean verificarPermissaoUsuarioImprimirRelatorioDefinitivo(Integer matricula, Short vinCodigo) throws ApplicationBusinessException, ServiceException ;
	
	Integer obterSeqAtendimentoPorConNumero(Integer conNumero) throws ApplicationBusinessException;
	
	void inserirLogImpressao(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp, String evento, Integer matricula, Short vinCodigo, Date criadoEm) throws ApplicationBusinessException;
	
	boolean gerarPendenciaDeAssinaturaDigital(Integer matricula, Short vinCodigo) throws ApplicationBusinessException;
	
	void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ApplicationBusinessException;
	
	void validarConsultaFinalizada(Integer pacCodigo, Short seqp, Integer numeroConsulta) throws ApplicationBusinessException;
	
	boolean moduloAgendamentoExameAtivo();
	
	void verificaPermissoesImpressaoRelatorio(Integer matricula, Short codigo, String usuarioLogado) throws ApplicationBusinessException;
	
	Integer obterAtendimentoPorNumConsulta(Integer numConsulta);

	boolean verificarSeModuloEstaAtivo(String nome) throws ApplicationBusinessException;

	void verificarPermissaoParaSolicitarExames(Integer matricula, Short vinCodigo) throws ApplicationBusinessException, ServiceException;
	
	Integer obterAtendimentoSeqPorNumeroDaConsulta(Integer numeroConsulta);

	void gravarNotaAdicional(Integer pacCodigo, Short gsoSeqp, Integer conNumero, DominioEventoNotaAdicional evento, String notaAdicional) throws ApplicationBusinessException;

	String buscarUltimoEventoParaCadastrarNotaAdicional(Integer pacCodigo, Short seqp, Integer numeroConsulta);
	
	boolean gerarPendenciaDeAssinaturaDigitalDoUsuarioLogado() throws ApplicationBusinessException;

	List<RecemNascidoVO> listarRecemNascidoVOs(RecemNascidoFilterVO filter)throws BaseException;

	List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicas(String param);

	List<AchadoVO> pesquisarAchados(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AchadoVO filtro);

	Long pesquisarAchadosCount(AchadoVO filtro);

	McoAchado obterAchadoPorChavePrimaria(Integer seq);

	void ativarInativarAchado(McoAchado achado) throws ApplicationBusinessException;

	void atualizarAchado(McoAchado achado) throws ApplicationBusinessException;

	void inserirAchado(McoAchado achado, String descricaoRegiao) throws ApplicationBusinessException;

	Long pesquisarRegioesAnatomicasCount(String param);

	Long listaMcoSindromeAtivaCount(Object param);

	List<McoSindrome> listarMcoSindromeAtiva(Object param);

	List<RegistrarExameFisicoVO> obterExameFisicoRN(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException;

	RegistrarExameFisicoVO validarRegistroExameFisico(RegistrarExameFisicoVO vo) throws ApplicationBusinessException;

	List<McoSindrome> listarSindrome(Integer seq, String descricao,
			String situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	Long listarSindromeCount(Integer seq, String descricao, String situacao);

	void persistirSindrome(McoSindrome entity);
	
	
	void adicionarRecemNascido(RecemNascidoVO vo, Integer quantidadeRemcemNascido)throws BaseException;
	
	void validarRecemNascido(RecemNascidoVO vo)throws BaseException;

	void excluirRecemNascido(RecemNascidoVO vo, List<RecemNascidoVO> listRecemNascidoVOs) throws BaseException;

	McoSindrome obterSindromePorDescricao(String descricao);

	McoSindrome obterSindromePorChavePrimaria(Integer seq);

	
	List<EscalaLeitoRecemNascidoVO> pesquisarEscalaLeitoRecemNascido(Integer firstResult, Integer maxResults, String orderProperty, 
			boolean asc, Servidor servidor, LeitoVO leito) throws ApplicationBusinessException;

	Long pesquisarEscalaLeitoRecemNascidoCount(Servidor servidor, LeitoVO leito) throws ApplicationBusinessException;

	void deletarMcoEscalaLeitoRecemNascidoPorId(McoEscalaLeitoRecemNascidoId id) throws BaseException;

	void inserirMcoEscalaLeitoRecemNascido(McoEscalaLeitoRecemNascido entity) throws ApplicationBusinessException;

	List<LeitoVO> pesquisarLeitos(String param) throws ApplicationBusinessException;

	Long pesquisarLeitosCount(String param) throws ApplicationBusinessException;

	List<Servidor> pesquisarServidores(String param) throws ApplicationBusinessException;

	Long pesquisarServidoresCount(String param) throws ApplicationBusinessException;

	void validarExclusao(Integer gsoPacCodigo, Short gsoSeqp, RegistrarExameFisicoVO vo) throws ApplicationBusinessException;

	boolean alterarRegistroExameFisico(RegistrarExameFisicoVO voAlterado, RegistrarExameFisicoVO voOriginal);

	McoTabBallard obterBallardPorChavePrimaria(Short seq);

	void removerBallard(Short seq);

	Long listarBallardCount(Short escore);

	List<McoTabBallard> listarBallard(Short escore, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente);
	
	McoRecemNascidos obterRecemNascidoPorId(McoRecemNascidosId id);

	Paciente obterPacienteRecemNascido(Integer pacCodigoRecemNascido)
			throws ApplicationBusinessException;

	List<McoApgars> obterListaApgarPorCodigoPaciente(Integer codigoPaciente, Integer matricula, Short vinculo);

	void persistirApgar(McoApgars apgar) throws BaseException;

	boolean validarAlteracaoRecebemNascido(RecemNascidoVO voAlterado, RecemNascidoVO voOriginal);
	
	List<RapServidorConselhoVO> pesquisarServidoresPorSiglaConselhoNumeroNome(String sigla, String param) throws ApplicationBusinessException;

	Integer pesquisarServidoresPorSiglaConselhoNumeroNomeCount(String sigla, String param) throws ApplicationBusinessException;
	
	void validarBCF(McoAnamneseEfs anamneseEfs, boolean permNrBfc, int tipoBCF)  throws ApplicationBusinessException;
	/**
	 * #28358 
	 */
	boolean verificarExisteRegistroRecemNascido(RecemNascidoVO vo);
	void validarConsultaFinalizada(Integer pacCodigo, Short seqp, Integer numeroConsulta, DominioEventoLogImpressao... eventos) throws ApplicationBusinessException;
	Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(Integer pacCodigoRecemNascido);
	/**
	 * #41973
	 */
	List<MedicamentoRecemNascidoVO> buscarMedicamentosPorDescricao(String descricao);
	List<ViaAdministracaoVO> pesquisarViaAdminstracaoSiglaouDescricao(String param);
	List<MedicamentoRecemNascidoVO> buscarMedicamentosPorRecemNascido(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp);
	Long buscarMedicamentosCountPorDescricao(String descricao);
	Long pesquisarViaAdminstracaoSiglaouDescricaoCount(String param);
	void validarMedicamento(MedicamentoRecemNascidoVO medicamento, List<MedicamentoRecemNascidoVO> lista) throws ApplicationBusinessException;
	boolean isMedicamentoRecemNascidoCadastrado(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp, Integer seqPni);
	
	List<McoAtendTrabPartos> buscarListaMcoAtendTrabPartos(Integer pacCodigo, Short seqp);
	
	
	List<TabAdequacaoPesoPercentilVO> listaTabAdequacaoPeso();

	List<SnappeElaboradorVO> listarSnappeElaboradorVO(Integer pacCodigo) throws ApplicationBusinessException;

	McoSnappes obterMcoSnappePorId(Integer pacCodigo, Short seqp);

	boolean verificarUsuarioAlteracaoSnappe(McoSnappes snappe);

	Integer calcularSnappe(McoSnappes snappe);

	void gravarSnappe(McoSnappes snappe, boolean novo) throws ApplicationBusinessException;

	void excluirSnappe(McoSnappes snappe) throws ApplicationBusinessException;

	McoSnappes obterSnappePrePreenchido(Integer pacCodigo)
			throws ApplicationBusinessException;

	Short obterMaxSeqpSnappesPorCodigoPaciente(Integer pacCodigo);

	boolean existeRegistroUsuarioSnappe(List<SnappeElaboradorVO> lista);

	boolean verificarAtualizacaoSnappe(McoSnappes snappe);
	
	List<CalculoCapurroVO> listarCalculoCapurrosPorCodigoPaciente(Integer pacCodigo);
	
	boolean possuiAlteracaoIddGestCapurros(CalculoCapurroVO calculoSelecionado);
	
	void persistirMcoIddGestCapurros(CalculoCapurroVO calculoCapurroVO) throws ApplicationBusinessException;
	
	void excluirMcoIddGestCapurros(CalculoCapurroVO calculoCapurroVO);

	boolean verificarSumariaAlta(McoSnappes snappe) throws ApplicationBusinessException;

	void validarListaUnidadesFuncionais(List<Short> listaSeqp)throws ApplicationBusinessException ;

	List<DataNascimentoVO> buscarDatasDoNascimentos(Integer pacCodigo,Short gsoSeqp);
	
	void verificaSeExisteCondutaSemComplementoNaoCadastrada(McoConduta conduta, McoPlanoIniciais planoIniciais) throws ApplicationBusinessException;
	
	void validarProsseguirComGravarNascimento(DadosNascimentoVO nascimento, McoCesarianas cesarianas)  throws ApplicationBusinessException;
	
	void validarFieldSetNascimento(DadosNascimentoSelecionadoVO dados) throws ApplicationBusinessException;
	
	void validarAgendamento(DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) throws ApplicationBusinessException;
	
	void validarAnestesia(Short tipoAnestesia) throws ApplicationBusinessException;
	
	void validarEquipe(EquipeVO equipe)  throws ApplicationBusinessException;

//	void validarDataProcedimento(DadosNascimentoSelecionadoVO dadosNascimentoSelecionado)throws ApplicationBusinessException;

	void confirmarProcedimento(Integer pacCodigo, Short gsoSeqp,
			String contaminacao, Date dthrInicioProcedimento, Short seqp,
			Short tempoProcedimento, Short tanSeq, Short seq,
			String tipoParto)  throws ServiceException ;

	void validarDados(Boolean indAcompanhante
			, Integer pacCodigo
			, Short gsoSeqp
			, DadosGestacaoVO gestacacao
			, EquipeVO equipeVO
			, DadosNascimentoVO nascimentoSelecionado)  throws ApplicationBusinessException, ServiceException;
	
	Boolean verificarChamarModalParaExamesVDRL(Integer numeroConsulta, String sigla) throws ApplicationBusinessException;
	
	void atualizarSituacaoPaciente(Integer numeroConsulta, String login) throws ServiceException, ApplicationBusinessException;

	void atualizarAtendimentoGestante(Integer numeroConsulta, Integer pacCodigo, Short seqp, 
			String nomeMicrocomputador, String obterLoginUsuarioLogado) throws ApplicationBusinessException;

	List<McoNascimentos> listarNascimentos(Integer codigoPaciente,
			Short sequence);

	List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorAdmissaoObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero);

	List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorConsultaObs(
			Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero);

	Long pesquisarAnamnesesEfsPorGestacoesPaginadaCount(Integer codigo,
			Short seqp);

	List<HistObstetricaVO> pesquisarAnamnesesEfsPorGestacoesPaginada(
			Integer gsoPacCodigo, Short gsoSeqp, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	McoAnamneseEfs obterAnamnesePorPaciente(Integer pacCodigo, Short gsoSeqp);

	List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(
			Integer pacCodigo, Short seqp);

	public Long pesquisarEspecialidadeListaSeqCount(List<Short> listaEspId, String objPesquisa);
	
	 public void persistirMcoTabAdequacaoPeso(McoTabAdequacaoPeso tabAdequacaoPeso) throws ApplicationBusinessException;
	 
	 public List<McoTabAdequacaoPeso> pesquisarAdequacaoPesoIgSemanas(Integer firstResult, Integer maxResults, String orderProperty,
				boolean asc, Short igSemanas);
	 
	 public Long pesquisarCapacidadesAdequacaoPesoIgSemanasCount(Short igSemanas);
	 
	 public McoTabAdequacaoPeso obterMcoTabAdequacaoPesoPorSeq(Short seq);
	 
	 public void excluirAdequacaoPeso(Short seq);


	boolean habilitarBotaoNotasAdicionais(Integer gsoPacCodigo, Short gsoSeqp, Byte rnaSeqp, Integer conNumero) throws ApplicationBusinessException;

	public boolean existeAlteracaoSnappe(McoSnappes newSnappes);
	
	public void validarSeTemPermissaoRealizarEvolucaoAmbulatorio() throws ApplicationBusinessException;
	
	public Long iniciaAtendimento(Long trgSeq, Integer conNumero, Integer atdSeq, Date dataHoraInicio, Short espSeq, Boolean isEmergenciaObstetrica, String hostName) throws ServiceException, NumberFormatException, BaseException;
	
	public void atualizarSituacaoPacienteEmConsulta(Long trgSeq, String micNome) throws ServiceException, ApplicationBusinessException;
	
	public Long geraRegistroAtendimento(Long trgSeq, Integer atdSeq, String hostName) throws ServiceException, ApplicationBusinessException;
	
	public List<DocumentosPacienteVO> obterListaReceitasAtestadosPaciente(Integer conNumero) throws ApplicationBusinessException;
}
