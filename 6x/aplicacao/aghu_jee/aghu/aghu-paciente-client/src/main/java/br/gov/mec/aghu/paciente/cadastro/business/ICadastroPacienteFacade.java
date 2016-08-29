package br.gov.mec.aghu.paciente.cadastro.business;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioTipoImpressao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.AipOrgaosEmissores;
import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipCeps;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.model.VAipLocalidadeUc;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public interface ICadastroPacienteFacade extends Serializable {

	/**
	 * @param pProntuarioOrigem
	 * @param pCodigoDestino
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	
	void inserirJournal(Integer pProntuarioOrigem, Integer pCodigoDestino)
			throws ApplicationBusinessException;

	/**
	 * @param codigoPaciente
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.PacienteJournalON#obterNomeAnteriorPaciente(java.lang.Integer)
	 */
	
	String obterNomeAnteriorPaciente(Integer codigoPaciente);

	/**
	 * @param atendimentoMae
	 * @param recemNascidos
	 * @throws BaseException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirRecemNascidos(br.gov.mec.aghu.model.AghAtendimentos, java.util.List)
	 */
	void persistirRecemNascidos(AghAtendimentos atendimentoMae,
			List<AipPacientes> recemNascidos, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	/**
	 * @param aipPaciente
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	
	
	AipPacientes persistirPaciente(AipPacientes aipPaciente, String nomeMicrocomputador)
			throws BaseException;
	
	/**
	 * @param aipPaciente
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	
	
	AipPacientes persistirPacienteCirurgico(AipPacientes aipPaciente)
			throws BaseException;

	/**
	 * @param enderecoPaciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#organizarValores(br.gov.mec.aghu.model.AipEnderecosPacientes)
	 */
	void organizarValores(AipEnderecosPacientes enderecoPaciente) throws ApplicationBusinessException;

	/**
	 * @param aipPaciente
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#atualizarPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	
	AipPacientes atualizarPaciente(AipPacientes aipPaciente, String nomeMicrocomputador, RapServidores servidorLogado, Boolean substituirProntuario)
			throws BaseException;

	/**
	 * @param dadosAdicionais
	 * @param aipPaciente
	 * @param aipPesoPacientes
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirDadosAdicionais(br.gov.mec.aghu.model.AipPacienteDadoClinicos, br.gov.mec.aghu.model.AipPacientes, br.gov.mec.aghu.model.AipPesoPacientes)
	 */
	
	void persistirDadosAdicionais(AipPacienteDadoClinicos dadosAdicionais,
			AipPacientes aipPaciente, AipPesoPacientes aipPesoPacientes)
			throws ApplicationBusinessException, BaseListException;

	/**
	 * @param aipPesoPacientes
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirPesoPaciente(br.gov.mec.aghu.model.AipPesoPacientes)
	 */
	
	void persistirPesoPaciente(AipPesoPacientes aipPesoPacientes, RapServidores servidorLogado)
			throws ApplicationBusinessException;

	/**
	 * @param aipPaciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirCartaoSus(br.gov.mec.aghu.model.AipPacientes)
	 */
	
	void persistirCartaoSus(AipPacientes aipPaciente)
			throws ApplicationBusinessException;

	/**
	 * @param aipPaciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#validarCartaoSUS(br.gov.mec.aghu.model.AipPacientes)
	 */
	void validarCartaoSUS(AipPacientes aipPaciente, AipPacientesDadosCns aipPacienteDanosCns) throws BaseException;
	
	void validarCartaoSUSRN(AipPacientes aipPaciente, AipPacientesDadosCns aipPacienteDanosCns) throws ApplicationBusinessException;
	
	/**
	 * @param aipPaciente
	 * @throws MECBaseException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#validarDuplicidadeCartaoSUS(AipPacientes)
	 */
	void validarDuplicidadeCartaoSUS(AipPacientes aipPaciente) throws ApplicationBusinessException;
	
	/**
	 * @param aipPaciente
	 * @throws MECBaseException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#validarDuplicidadeCPF(AipPacientes)
	 */
	void validarDuplicidadeCPF(AipPacientes aipPaciente) throws ApplicationBusinessException;	

	/**
	 * @param planosPaciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirPlanoPaciente(java.util.List)
	 */
	
	void persistirPlanoPaciente(List<AipConveniosSaudePaciente> planosPaciente)
			throws ApplicationBusinessException;

	/**
	 * @param planoPaciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#incluirPlanoPacienteInternacao(br.gov.mec.aghu.model.AipConveniosSaudePaciente)
	 */
	
	void incluirPlanoPacienteInternacao(AipConveniosSaudePaciente planoPaciente)
			throws ApplicationBusinessException;

	/**
	 * @param pacCodigo
	 * @param seq
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#removerPlanoPaciente(java.lang.Integer, java.lang.Short)
	 */
	
	
	void removerPlanoPaciente(Integer pacCodigo, Short seq)
			throws ApplicationBusinessException;

	/**
	 * @param paramPesquisa
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#pesquisarOrgaoEmissorPorCodigoDescricao(java.lang.Object)
	 */
	
	List<AipOrgaosEmissores> pesquisarOrgaoEmissorPorCodigoDescricao(
			Object paramPesquisa);

	/**
	 * @param paramPesquisa
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#obterCountOrgaoEmissorPorCodigoDescricao(java.lang.Object)
	 */
	
	Long obterCountOrgaoEmissorPorCodigoDescricao(Object paramPesquisa);

	/**
	 * @param aipPacienteDadosCns
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#validaTipoCertidaoCartaoSus(br.gov.mec.aghu.model.AipPacientesDadosCns)
	 */
	void validaTipoCertidaoCartaoSus(AipPacientesDadosCns aipPacienteDadosCns)
			throws BaseException;

	/**
	 * @param codigo
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#recuperarPaciente(java.lang.Integer)
	 */
	
	
	void recuperarPaciente(Integer codigo) throws ApplicationBusinessException;

	/**
	 * @param paciente
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirPacienteSemValidacao(br.gov.mec.aghu.model.AipPacientes)
	 */
	
	
	void persistirPacienteSemValidacao(AipPacientes paciente);

	/**
	 * @param paciente
	 * @throws BaseException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#atualizarSitCadPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	void atualizarSitCadPaciente(AipPacientes paciente, String usuarioLogado) throws BaseException;

	/**
	 * @param solicitacaoProntuarios
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#removerSolicitacaoProntuario(br.gov.mec.aghu.model.AipSolicitacaoProntuarios)
	 */
	
	
	void removerSolicitacaoProntuario(
			AipSolicitacaoProntuarios solicitacaoProntuarios)
			throws ApplicationBusinessException;

	/**
	 * @param solicitacaoProntuarios
	 * @param listaPacientesRemovidos
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirSolicitacaoProntuario(br.gov.mec.aghu.model.AipSolicitacaoProntuarios, java.util.List)
	 */
	
	
	void persistirSolicitacaoProntuario(
			AipSolicitacaoProntuarios solicitacaoProntuarios,
			List<AipPacientes> listaPacientesRemovidos)
			throws ApplicationBusinessException;

	/**
	 * @param lista
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#validaProntuariosSolicitacao(java.util.List)
	 */
	void validaProntuariosSolicitacao(List<AipPacientes> lista)
			throws ApplicationBusinessException;

	/**
	 * @param pacienteHist
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#persistirPacienteDeHistoricoPaciente(br.gov.mec.aghu.model.AipPacientesHist)
	 */
	
	
	void persistirPacienteDeHistoricoPaciente(AipPacientesHist pacienteHist)
			throws ApplicationBusinessException;

	/**
	 * @param tipoImpressao
	 * @param nroVolumeSelecionado
	 * @param nroVolumesPaciente
	 * @param prontuario
	 * @param nome
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#imprimirEtiquetasPaciente(br.gov.mec.aghu.dominio.DominioTipoImpressao, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	String imprimirEtiquetasPaciente(DominioTipoImpressao tipoImpressao,
			Integer nroVolumeSelecionado, Integer nroVolumesPaciente,
			Integer prontuario, String nome) throws ApplicationBusinessException;

	/**
	 * @param tipoImpressao
	 * @param nroVolumeSelecionado
	 * @param nroVolumesPaciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#validarImpressaoEtiquetas(br.gov.mec.aghu.dominio.DominioTipoImpressao, java.lang.Integer, java.lang.Integer)
	 */
	void validarImpressaoEtiquetas(DominioTipoImpressao tipoImpressao,
			Integer nroVolumeSelecionado, Integer nroVolumesPaciente)
			throws ApplicationBusinessException;

	/**
	 * @param igSemanas
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#definirMesesGestacaoDadosAdicionais(java.lang.Byte)
	 */
	Byte definirMesesGestacaoDadosAdicionais(Byte igSemanas);

	/**
	 * @param codigoPaciente
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteON#pesquisarAtendimentosPorPaciente(java.lang.Integer)
	 */
	
	List<AghAtendimentos> pesquisarAtendimentosPorPaciente(
			Integer codigoPaciente);

	/**
	 * @param paciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#inserirPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	void inserirPaciente(AipPacientes paciente) throws ApplicationBusinessException;

	/**
	 * @param paciente
	 * @param pessoa
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#atualizarPaciente(br.gov.mec.aghu.model.AipPacientes, br.gov.mec.aghu.recursoshumanos.Pessoa)
	 */
	void atualizarPacienteParcial(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException;

	/**
	 * @param numeroCartao
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#validarNumeroCartaoSaude(java.math.BigInteger)
	 */
	void validarNumeroCartaoSaude(BigInteger numeroCartao) throws ApplicationBusinessException;
	
	/**
	 * @param paciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#excluirPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	void excluirPaciente(AipPacientes paciente, String usuarioLogado) throws ApplicationBusinessException;

	/**
	 * @param codigo
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#obterPacienteAnterior(java.lang.Integer)
	 */
	AipPacientes obterPacienteAnterior(Integer codigo);

	/**
	 * @param pacienteAtual
	 * @param pacienteAnterior
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#pacienteModificado(br.gov.mec.aghu.model.AipPacientes, br.gov.mec.aghu.model.AipPacientes)
	 */
	boolean pacienteModificado(AipPacientes pacienteAtual,
			AipPacientes pacienteAnterior);

	/**
	 * @param paciente
	 * @param nome
	 * @param clazz
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#salvarFonemas(br.gov.mec.aghu.model.AipPacientes, java.lang.String, java.lang.Class)
	 */
	void salvarFonemas(AipPacientes paciente, String nome, Class clazz)
			throws ApplicationBusinessException;

	/**
	 * @param nome
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#pesquisarCidadePorNome(java.lang.Object)
	 */
	List<AipCidades> pesquisarCidadePorNome(Object nome);

	/**
	 * @param cep
	 * @param codigoCidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#pesquisarCeps(java.lang.Integer, java.lang.Integer)
	 */
	List<AipBairrosCepLogradouro> pesquisarCeps(Integer cep,
			Integer codigoCidade) throws ApplicationBusinessException;

	/**
	 * @param cep
	 * @param codigoCidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#obterCountCeps(java.lang.Integer, java.lang.Integer)
	 */
	Integer obterCountCeps(Integer cep, Integer codigoCidade)
			throws ApplicationBusinessException;

	/**
	 * @param enderecoPaciente
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#validarEndereco(br.gov.mec.aghu.model.AipEnderecosPacientes)
	 */
	void validarEndereco(AipEnderecosPacientes enderecoPaciente, Boolean validarMovimentacaoMunicipio)
			throws ApplicationBusinessException;
	
	/**
	 * @param enderecos
	 * @throws AGHUNegocioException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#validarQuantidadeEnderecoResidencial(java.util.Set)
	 */
	void validarQuantidadeEnderecoResidencial(
			Set<AipEnderecosPacientes> enderecos) throws BaseException;

	/**
	 * @param enderecos
	 * @throws AGHUNegocioException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#validarQuantidadeEnderecoCorrespondencia(java.util.Set)
	 */
	void validarQuantidadeEnderecoCorrespondencia(
			Set<AipEnderecosPacientes> enderecos) throws BaseException;

	/**
	 * @param paciente
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#atribuirSequencialEnderecos(br.gov.mec.aghu.model.AipPacientes)
	 */
	void atribuirSequencialEnderecos(AipPacientes paciente);

	/**
	 * @param pacCodigo
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#obterEndPaciente(java.lang.Integer)
	 */
	
	List<AipEnderecosPacientes> obterEndPaciente(Integer pacCodigo);

	/**
	 * @param paciente
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#obterSeqEnderecoPacienteAtual(br.gov.mec.aghu.model.AipPacientes)
	 */
	short obterSeqEnderecoPacienteAtual(AipPacientes paciente);

	/**
	 * @param codigoPaciente
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#obterEndecoPaciente(java.lang.Integer)
	 */
	
	VAipEnderecoPaciente obterEndecoPaciente(Integer codigoPaciente);

	/**
	 * @param delete
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#aiptEnpArd(br.gov.mec.aghu.model.AipEnderecosPacientes)
	 */
	void removerEnderecoPaciente(AipEnderecosPacientes delete);

	/**
	 * @param old
	 * @param update
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#aiptEnpAru(br.gov.mec.aghu.model.AipEnderecosPacientes, br.gov.mec.aghu.model.AipEnderecosPacientes)
	 */
	void aiptEnpAru(AipEnderecosPacientes old, AipEnderecosPacientes update);

	/**
	 * @param descricao
	 * @param codigoCidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#pesquisarLogradourosCepPorDescricaoCidade(java.lang.String, java.lang.Integer)
	 */
	List<AipBairrosCepLogradouro> pesquisarLogradourosCepPorDescricaoCidade(
			String descricao, Integer codigoCidade) throws ApplicationBusinessException;

	/**
	 * @param cep
	 * @param codigoCidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#pesquisarCepsPorCidade(java.lang.String, java.lang.Integer)
	 */
	List<VAipLocalidadeUc> pesquisarCepsPorCidade(String param, Integer codigoCidade)
			throws ApplicationBusinessException;

	/**
	 * @param cep
	 * @param codigoCidade
	 * @return
	 * @throws AGHUNegocioException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#pesquisarCepUnicoPorCidade(, java.lang.Integer)
	 */
	VAipCeps pesquisarCepUnicoPorCidade(Integer codigoCidade)
			throws ApplicationBusinessException;
	
	/**
	 * @param cep
	 * @param codigoCidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#obterCountCepsPorCidade(java.lang.String, java.lang.Integer)
	 */
	Integer obterCountCepsPorCidade(String param, Integer codigoCidade)
			throws ApplicationBusinessException;

	/**
	 * @param bairro
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#recarregarCepLogradouro(br.gov.mec.aghu.model.AipBairrosCepLogradouro)
	 */
	AipBairrosCepLogradouro recarregarCepLogradouro(
			AipBairrosCepLogradouro bairro);

	/**
	 * @param paciente
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#obterEnderecoResidencialPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	
	AipEnderecosPacientes obterEnderecoResidencialPaciente(AipPacientes paciente);

	/**
	 * @param idEnderecoAnterior
	 * @return
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#obterEnderecoBanco(br.gov.mec.aghu.model.AipEnderecosPacientesId)
	 */
	
	AipEnderecosPacientes obterEnderecoBanco(
			AipEnderecosPacientesId idEnderecoAnterior);

	/**
	 * @param endOld
	 * @param end
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.EnderecoON#atualizarEndereco(br.gov.mec.aghu.model.AipEnderecosPacientes, br.gov.mec.aghu.model.AipEnderecosPacientes)
	 */
	
	
	void atualizarEndereco(AipEnderecosPacientes endOld,
			AipEnderecosPacientes end) throws BaseException;

	void verificarCamposInclusaoEndereco(Integer cloCep, String logradouro,
			String cidade, Integer codigoCidade, Integer pacCodigo)
			throws ApplicationBusinessException;

	boolean verificarPacienteModificado(AipPacientes pacienteAtual);

	public AipPacienteDadoClinicos buscaDadosAdicionaisClinicos(AipPacientes paciente);

	void persistirAlturaPaciente(AipAlturaPacientes alturaPaciente)
			throws ApplicationBusinessException;

	public void excluirEndereco(AipEnderecosPacientes endereco);
	
	boolean existeEnderecoPaciente(AipEnderecosPacientesId enderecoPacienteId);
	
	public void persistirPacienteFonemas(AipPacientes paciente) throws ApplicationBusinessException;
	
	public void inserirPacienteMigracao(AipPacientes paciente);
	

	public List<VAipCeps> pesquisarVAipCeps(Integer cep, String logradouro, String cidade) throws ApplicationBusinessException;

	public Integer obterCountVAipCeps(Integer cep, String logradouro, String cidade) throws ApplicationBusinessException;

	public VAipCeps obterVAipCeps(Integer cep, Integer codLogradouro,
			Integer codBairroLogradouro) throws ApplicationBusinessException;

	public List<VAipCeps> pesquisarVAipCepsPorLogradouroCidade(
			String logradouro, Integer codigoCidade) throws ApplicationBusinessException;

	public List<AipCidades> pesquisarCidadesOrdenadas(Object param, Integer cidadeHu, String siglaUfHu);
	
	public AipBairrosCepLogradouro obterBairroCepLogradouroPorCepBairroLogradouro(
			Integer cep, Integer codigoBairroLogradouro,
			Integer codigoLogradouro);

	public AipBairrosCepLogradouro obterBairroCepLogradouroPorCep(Integer cep);
	public Boolean nomeCidadeJaExistente(String nomeCidade);
	
	void validarCepEnderecoNaoCadastrado(Integer cep) throws ApplicationBusinessException;
	
	Integer gerarNumeroProntuarioVirtualPacienteEmergencia(AipPacientes paciente, String nomeMicrocomputador) throws ApplicationBusinessException;

	VAipEnderecoPaciente obterEndecoPacienteSemValidacaoPermissao(
			Integer codigoPaciente);
	

	void atualizarPaciente(AipPacientes paciente, String nomeMicrocomputador, Date dataFimVinculoServidor) throws ApplicationBusinessException;
	
	/**
	* Verifica se o campo "Prontuário Legado / Prontuário Família" deve ser exibido
	* @return
	*/
	public boolean verificarProntuarioFamiliaVisivel(String nomeMicrocomputador);

	public List<VAipCeps> pesquisarVAipCepsPorCidade(Integer codigoCidade) throws ApplicationBusinessException;
	
	AipBairrosCepLogradouro obterAipBairrosCepLogradouroPorId(AipBairrosCepLogradouroId id);
	
	public AipEnderecosPacientes obterEnderecoResidencialPadraoPaciente(AipPacientes paciente);
	
	public AipOrgaosEmissores obterOrgaoEmissorPorCodigo(Short codigo);
	
	List<AipCidades> pesquisarCidadesPorNome(String nome);
	
}