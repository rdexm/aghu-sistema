package br.gov.mec.aghu.paciente.cadastro.business;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
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
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipBairrosCepLogradouroDAO;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipOrgaosEmissoresDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;



@Modulo(ModuloEnum.PACIENTES)
@SuppressWarnings({"PMD.ExcessiveClassLength"})
@Stateless
public class CadastroPacienteFacade extends BaseFacade implements ICadastroPacienteFacade {


	@EJB
	private PacienteJournalON pacienteJournalON;
	
	@EJB
	private EnderecoON enderecoON;
	
	@EJB
	private CadastroPacienteRN cadastroPacienteRN;
	
	@EJB
	private CadastroPacienteON cadastroPacienteON;
	
	@EJB
	private PesoPacienteRN pesoPacienteRN;
	
	@Inject
	private AipCidadesDAO aipCidadesDAO;
	
	@Inject
	private AipBairrosCepLogradouroDAO aipBairrosCepLogradouroDAO;
	
	@Inject
	private AipPesoPacientesDAO aipPesoPacientesDAO;
	
	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;
	
	@Inject
	private AipAlturaPacientesDAO aipAlturaPacientesDAO;
	
	@Inject
	private AipOrgaosEmissoresDAO aipOrgaosEmissoresDAO;
	
	
	private static final long serialVersionUID = 3569348771923024065L;

	protected CadastroPacienteON getCadastroPacienteON() {
		return cadastroPacienteON;
	}

	protected CadastroPacienteRN getCadastroPacienteRN() {
		return cadastroPacienteRN;
	}

	protected EnderecoON getEnderecoON() {
		return enderecoON;
	}

	protected PacienteJournalON getPacienteJournalON() {
		return pacienteJournalON;
	}

	protected PesoPacienteRN getPesoPacienteRN() {
		return pesoPacienteRN;
	}

	@Override
	@Secure("#{s:hasPermission('journalPaciente','alterar')}")
	public void inserirJournal(Integer pProntuarioOrigem, Integer pCodigoDestino) throws ApplicationBusinessException {
		this.getPacienteJournalON().inserirJournal(pProntuarioOrigem, pCodigoDestino);
	}

	@Override
	@Secure("#{s:hasPermission('journalPaciente','pesquisar')}")
	public String obterNomeAnteriorPaciente(Integer codigoPaciente) {
		return this.getPacienteJournalON().obterNomeAnteriorPaciente(codigoPaciente);
	}

	@Override
	public void persistirRecemNascidos(AghAtendimentos atendimentoMae, List<AipPacientes> recemNascidos, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.getCadastroPacienteON().persistirRecemNascidos(atendimentoMae, recemNascidos, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('paciente','alterar') or s:hasPermission('gerarExamesIntegracao','inserir')}")
	public AipPacientes persistirPaciente(AipPacientes aipPaciente, String nomeMicrocomputador) throws BaseException {
		return this.getCadastroPacienteON().persistirPaciente(aipPaciente, nomeMicrocomputador);
	}
	
	@Override
	@Secure("#{s:hasPermission('paciente','alterar') or s:hasPermission('gerarExamesIntegracao','inserir')}")
	public AipPacientes persistirPacienteCirurgico(AipPacientes aipPaciente) throws BaseException {
		return this.getCadastroPacienteON().persistirPacienteCirurgico(aipPaciente);
	}

	@Override
	public void organizarValores(AipEnderecosPacientes enderecoPaciente) throws ApplicationBusinessException {
		this.getCadastroPacienteON().organizarValores(enderecoPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public AipPacientes atualizarPaciente(AipPacientes aipPaciente,
			String nomeMicrocomputador, RapServidores servidorLogado,
			Boolean substituirProntuario) throws BaseException {
		return this.getCadastroPacienteON().atualizarPaciente(aipPaciente, nomeMicrocomputador,servidorLogado, substituirProntuario);
	}

	@Override
	public void atualizarPaciente(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		getCadastroPacienteRN().atualizarPaciente(paciente, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	
	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public void persistirDadosAdicionais(AipPacienteDadoClinicos dadosAdicionais, AipPacientes aipPaciente,
			AipPesoPacientes aipPesoPacientes) throws ApplicationBusinessException, BaseListException {
		this.getCadastroPacienteON().persistirDadosAdicionais(dadosAdicionais, aipPaciente, aipPesoPacientes);
	}

	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public void persistirPesoPaciente(AipPesoPacientes aipPesoPacientes, RapServidores servidorLogado) throws ApplicationBusinessException {
		this.getCadastroPacienteON().persistirPesoPaciente(aipPesoPacientes,servidorLogado);
	}
	
	@Override
	public void persistirAlturaPaciente(AipAlturaPacientes alturaPaciente) throws ApplicationBusinessException {
		this.getCadastroPacienteON().persistirAlturaPaciente(alturaPaciente);
	}
	
	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public void persistirCartaoSus(AipPacientes aipPaciente) throws ApplicationBusinessException {
		this.getCadastroPacienteON().persistirCartaoSus(aipPaciente);
	}

	@Override
	public void validarCartaoSUS(AipPacientes aipPaciente, AipPacientesDadosCns aipPacienteDadosCns) throws BaseException{
		this.getCadastroPacienteON().validarCartaoSUS(aipPaciente, aipPacienteDadosCns);
	}
	
	@Override
	public void validarCartaoSUSRN(AipPacientes aipPaciente, AipPacientesDadosCns aipPacienteDadosCns) throws ApplicationBusinessException{
		this.getCadastroPacienteON().validarCartaoSUSRN(aipPaciente, aipPacienteDadosCns);
	}
	
	@Override
	public void validarDuplicidadeCartaoSUS(AipPacientes aipPaciente) throws ApplicationBusinessException{
		this.getCadastroPacienteON().validarDuplicidadeCartaoSUS(aipPaciente);
	}
	
	@Override
	public void validarDuplicidadeCPF(AipPacientes aipPaciente) throws ApplicationBusinessException{
		this.getCadastroPacienteON().validarDuplicidadeCPF(aipPaciente);
	}
	

	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public void persistirPlanoPaciente(List<AipConveniosSaudePaciente> planosPaciente) throws ApplicationBusinessException {
		this.getCadastroPacienteON().persistirPlanoPaciente(planosPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public void incluirPlanoPacienteInternacao(AipConveniosSaudePaciente planoPaciente) throws ApplicationBusinessException {
		this.getCadastroPacienteON().incluirPlanoPacienteInternacao(planoPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public void removerPlanoPaciente(Integer pacCodigo, Short seq) throws ApplicationBusinessException {
		this.getCadastroPacienteON().removerPlanoPaciente(pacCodigo, seq);
	}

	@Override
	@Secure("#{s:hasPermission('orgaoEmissor','pesquisar')}")
	public List<AipOrgaosEmissores> pesquisarOrgaoEmissorPorCodigoDescricao(Object paramPesquisa) {
		return this.getCadastroPacienteON().pesquisarOrgaoEmissorPorCodigoDescricao(paramPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('orgaoEmissor','pesquisar')}")
	public Long obterCountOrgaoEmissorPorCodigoDescricao(Object paramPesquisa) {
		return this.getCadastroPacienteON().obterCountOrgaoEmissorPorCodigoDescricao(paramPesquisa);
	}

	@Override
	public void validaTipoCertidaoCartaoSus(AipPacientesDadosCns aipPacienteDadosCns) throws BaseException {
		this.getCadastroPacienteON().validaTipoCertidaoCartaoSus(aipPacienteDadosCns);
	}

	@Override
	@Secure("#{s:hasPermission('paciente','pesquisar')}")
	public void recuperarPaciente(Integer codigo) throws ApplicationBusinessException {
		this.getCadastroPacienteON().recuperarPaciente(codigo);
	}

	@Override
	public void persistirPacienteSemValidacao(AipPacientes paciente) {
		this.getCadastroPacienteON().persistirPacienteSemValidacao(paciente);
	}

	@Override
	public void atualizarSitCadPaciente(AipPacientes paciente, String usuarioLogado) throws BaseException {
		this.getCadastroPacienteON().atualizarSitCadPaciente(paciente, usuarioLogado);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoProntuario','excluir')}")
	public void removerSolicitacaoProntuario(AipSolicitacaoProntuarios solicitacaoProntuarios) throws ApplicationBusinessException {
		this.getCadastroPacienteON().removerSolicitacaoProntuario(solicitacaoProntuarios);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoProntuario','alterar')}")
	public void persistirSolicitacaoProntuario(AipSolicitacaoProntuarios solicitacaoProntuarios,
			List<AipPacientes> listaPacientesRemovidos) throws ApplicationBusinessException {
		this.getCadastroPacienteON().persistirSolicitacaoProntuario(solicitacaoProntuarios, listaPacientesRemovidos);
	}

	@Override
	public void validaProntuariosSolicitacao(List<AipPacientes> lista) throws ApplicationBusinessException {
		this.getCadastroPacienteON().validaProntuariosSolicitacao(lista);
	}

	@Override
	@Secure("#{s:hasPermission('historicoPaciente','pesquisar')}")
	public void persistirPacienteDeHistoricoPaciente(AipPacientesHist pacienteHist) throws ApplicationBusinessException {
		this.getCadastroPacienteON().persistirPacienteDeHistoricoPaciente(pacienteHist);
	}

	@Override
	public String imprimirEtiquetasPaciente(DominioTipoImpressao tipoImpressao, Integer nroVolumeSelecionado,
			Integer nroVolumesPaciente, Integer prontuario, String nome) throws ApplicationBusinessException {
		return this.getCadastroPacienteON().imprimirEtiquetasPaciente(tipoImpressao, nroVolumeSelecionado, nroVolumesPaciente, prontuario,
				nome);
	}

	@Override
	public void validarImpressaoEtiquetas(DominioTipoImpressao tipoImpressao, Integer nroVolumeSelecionado, Integer nroVolumesPaciente)
			throws ApplicationBusinessException {
		this.getCadastroPacienteON().validarImpressaoEtiquetas(tipoImpressao, nroVolumeSelecionado, nroVolumesPaciente);
	}

	@Override
	public Byte definirMesesGestacaoDadosAdicionais(Byte igSemanas) {
		return this.getCadastroPacienteON().definirMesesGestacaoDadosAdicionais(igSemanas);
	}

	@Override
	@Secure("#{s:hasPermission('atendimento','pesquisar')}")
	public List<AghAtendimentos> pesquisarAtendimentosPorPaciente(Integer codigoPaciente) {
		return this.getCadastroPacienteON().pesquisarAtendimentosPorPaciente(codigoPaciente);
	}

	@Override
	public void inserirPaciente(AipPacientes paciente) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().inserirPaciente(paciente);
	}

	@Override
	public void atualizarPacienteParcial(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().atualizarPaciente(paciente, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * @param numeroCartao
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#validarNumeroCartaoSaude(java.math.BigInteger)
	 */
	@Override
	public void validarNumeroCartaoSaude(BigInteger numeroCartao) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().validarNumeroCartaoSaude(numeroCartao);
	}

	@Override
	public void excluirPaciente(AipPacientes paciente, String usuarioLogado) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().excluirPaciente(paciente, usuarioLogado);
	}

	/**
	 * @param prontuario
	 * @return
	 * @throws ApplicationBusinessException 
	 * @see br.gov.mec.aghu.paciente.cadastro.business.CadastroPacienteRN#liberarProntuario(java.lang.Integer)
	 */
	public boolean liberarProntuario(Integer prontuario) throws ApplicationBusinessException {
		return this.getCadastroPacienteRN().liberarProntuario(prontuario);
	}

	@Override
	public AipPacientes obterPacienteAnterior(Integer codigo) {
		return this.getCadastroPacienteRN().obterPacienteAnterior(codigo);
	}

	@Override
	public boolean pacienteModificado(AipPacientes pacienteAtual, AipPacientes pacienteAnterior) {
		return this.getCadastroPacienteRN().pacienteModificado(pacienteAtual, pacienteAnterior);
	}

	@Override
	public void salvarFonemas(AipPacientes paciente, String nome, Class clazz) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().salvarFonemas(paciente, nome, clazz);
	}
	
	@Override
	public List<AipCidades> pesquisarCidadePorNome(Object nome) {
		return this.getEnderecoON().pesquisarCidadePorNome(nome);
	}

	@Override
	public List<AipCidades> pesquisarCidadesOrdenadas(Object param, Integer cidadeHu, String siglaUfHu) {
		List<AipCidades> listCidades = this.getEnderecoON().pesquisarCidadePorNome(param);
		return this.getCadastroPacienteRN().ordenarCidades(listCidades, cidadeHu, siglaUfHu);
	}
	
	@Override
	public List<AipBairrosCepLogradouro> pesquisarCeps(Integer cep, Integer codigoCidade) throws ApplicationBusinessException {
		return this.getEnderecoON().pesquisarCeps(cep, codigoCidade);
	}

	@Override
	public Integer obterCountCeps(Integer cep, Integer codigoCidade) throws ApplicationBusinessException {
		return this.getEnderecoON().obterCountCeps(cep, codigoCidade);
	}

	@Override
	public void validarEndereco(AipEnderecosPacientes enderecoPaciente, Boolean validarMovimentacaoMunicipio) throws ApplicationBusinessException {
		this.getEnderecoON().validarEndereco(enderecoPaciente, validarMovimentacaoMunicipio);
	}
	
	@Override
	public void validarCepEnderecoNaoCadastrado(Integer cep) throws ApplicationBusinessException {
		this.getEnderecoON().validarCepEnderecoNaoCadastrado(cep);
	}
	
	@Override
	public void validarQuantidadeEnderecoResidencial(Set<AipEnderecosPacientes> enderecos) throws BaseException {
		this.getEnderecoON().validarQuantidadeEnderecoResidencial(enderecos);
	}

	@Override
	public void validarQuantidadeEnderecoCorrespondencia(Set<AipEnderecosPacientes> enderecos) throws BaseException {
		this.getEnderecoON().validarQuantidadeEnderecoCorrespondencia(enderecos);
	}
	
	@Override
	public Integer gerarNumeroProntuarioVirtualPacienteEmergencia(AipPacientes paciente,
			String nomeMicrocomputador) throws ApplicationBusinessException {
		
		return this.getCadastroPacienteON().gerarNumeroProntuarioVirtualPacienteEmergencia(paciente, nomeMicrocomputador);
	}
	
	@Override
	public void atribuirSequencialEnderecos(AipPacientes paciente) {
		this.getEnderecoON().atribuirSequencialEnderecos(paciente);
	}

	@Override
	@Secure("#{s:hasPermission('enderecoPaciente','pesquisar')}")
	public List<AipEnderecosPacientes> obterEndPaciente(Integer pacCodigo) {
		return this.getEnderecoON().obterEndPaciente(pacCodigo);
	}

	@Override
	public short obterSeqEnderecoPacienteAtual(AipPacientes paciente) {
		return this.getEnderecoON().obterSeqEnderecoPacienteAtual(paciente);
	}

	@Override
	@Secure("#{s:hasPermission('enderecoPaciente','pesquisar')}")
	public VAipEnderecoPaciente obterEndecoPaciente(Integer codigoPaciente) {
		return this.getEnderecoON().obterEndecoPaciente(codigoPaciente);
	}

	@Override
	public VAipEnderecoPaciente obterEndecoPacienteSemValidacaoPermissao(Integer codigoPaciente) {
		return this.getEnderecoON().obterEndecoPaciente(codigoPaciente);
	}

	@Override
	public void removerEnderecoPaciente(AipEnderecosPacientes delete) {
		this.getEnderecoON().removerEnderecoPaciente(delete);
	}

	@Override
	public void aiptEnpAru(AipEnderecosPacientes old, AipEnderecosPacientes update) {
		this.getEnderecoON().aiptEnpAru(old, update);
	}

	@Override
	public List<AipBairrosCepLogradouro> pesquisarLogradourosCepPorDescricaoCidade(String descricao, Integer codigoCidade)
			throws ApplicationBusinessException {
		return this.getEnderecoON().pesquisarLogradourosCepPorDescricaoCidade(descricao, codigoCidade);
	}

	@Override
	public List<VAipLocalidadeUc> pesquisarCepsPorCidade(String param, Integer codigoCidade) throws ApplicationBusinessException {
		return this.getEnderecoON().pesquisarCepsPorCidade(param, codigoCidade);
	}

	@Override
	public Integer obterCountCepsPorCidade(String param, Integer codigoCidade) throws ApplicationBusinessException {
		return this.getEnderecoON().obterCountCepsPorCidade(param, codigoCidade);
	}

	@Override
	public AipBairrosCepLogradouro recarregarCepLogradouro(AipBairrosCepLogradouro bairro) {
		return this.getEnderecoON().recarregarCepLogradouro(bairro);
	}

	@Override
	@Secure("#{s:hasPermission('enderecoPaciente','pesquisar')}")
	public AipEnderecosPacientes obterEnderecoResidencialPaciente(AipPacientes paciente) {
		return this.getEnderecoON().obterEnderecoResidencialPaciente(paciente);
	}

	@Override
	@Secure("#{s:hasPermission('enderecoPaciente','pesquisar')}")
	public AipEnderecosPacientes obterEnderecoBanco(AipEnderecosPacientesId idEnderecoAnterior) {
		return this.getEnderecoON().obterEnderecoBanco(idEnderecoAnterior);
	}

	@Override
	@Secure("#{s:hasPermission('enderecoPaciente','alterar')}")
	public void atualizarEndereco(AipEnderecosPacientes endOld, AipEnderecosPacientes end) throws BaseException {
		this.getEnderecoON().atualizarEndereco(endOld, end);
	}

	@Override
	public void verificarCamposInclusaoEndereco(Integer cloCep, String logradouro, String cidade, Integer codigoCidade, Integer pacCodigo) throws ApplicationBusinessException{
		this.getEnderecoON().verificarCamposInclusaoEndereco(cloCep, logradouro, cidade, codigoCidade, pacCodigo);
	}
	
	@Override
	public boolean verificarPacienteModificado(AipPacientes pacienteAtual) {
		return this.getCadastroPacienteON().verificarPacienteModificado(pacienteAtual);
	}
	
	protected AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}

	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}
	
	@Override	
	public AipPacienteDadoClinicos buscaDadosAdicionaisClinicos(AipPacientes paciente){
		return getCadastroPacienteRN().buscaDadosAdicionaisClinicos(paciente);
	}

	
	@Override
	public void excluirEndereco(AipEnderecosPacientes endereco) {
		getEnderecoON().excluirEndereco(endereco);
	}
	
	@Override
	public boolean existeEnderecoPaciente(
			AipEnderecosPacientesId enderecoPacienteId) {
		return this.getAipEnderecosPacientesDAO().existeEnderecoPaciente(enderecoPacienteId);
	}
	
	protected AipEnderecosPacientesDAO getAipEnderecosPacientesDAO() {
		return aipEnderecosPacientesDAO;
	}
	
	@Override
	public void persistirPacienteFonemas(AipPacientes paciente) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().persistirPacienteFonemas(paciente);
	}
	
	@Override
	public void inserirPacienteMigracao(AipPacientes paciente){
		this.getCadastroPacienteRN().inserirPacienteMigracao(paciente);
	}
	
	@Override
	public Boolean nomeCidadeJaExistente(String nomeCidade) {
		return this.getEnderecoON().nomeCidadeJaExistente(nomeCidade);
	}

	@Override
	public List<VAipCeps> pesquisarVAipCeps(Integer cep, String logradouro, String cidade) throws ApplicationBusinessException {
		return getAipBairrosCepLogradouroDAO().pesquisarVAipCeps(cep, logradouro, cidade);
	}

	@Override
	public Integer obterCountVAipCeps(Integer cep, String logradouro, String cidade) throws ApplicationBusinessException{
		return getAipBairrosCepLogradouroDAO().obterCountVAipCeps(cep, logradouro, cidade);
	}

	@Override
	public VAipCeps obterVAipCeps(Integer cep, Integer codLogradouro, Integer codBairroLogradouro) throws ApplicationBusinessException{
		VAipCeps vAipCeps = getAipBairrosCepLogradouroDAO().obterVAipCeps(cep, codLogradouro, codBairroLogradouro);
		if(vAipCeps != null){
			vAipCeps.setAipBairrosCepLogradouro(getAipBairrosCepLogradouroDAO()
					.obterBairroCepLogradouroPorCepBairroLogradouro(cep, codBairroLogradouro, codLogradouro));
		}
		return vAipCeps;
	}

	@Override

	public List<VAipCeps> pesquisarVAipCepsPorCidade(Integer codigoCidade)
			throws ApplicationBusinessException {
		return this.getEnderecoON().pesquisarVAipCepsPorCidade(codigoCidade);
	}

	@Override
	public List<VAipCeps> pesquisarVAipCepsPorLogradouroCidade(String descricao, Integer codigoCidade)
			throws ApplicationBusinessException {
		return this.getEnderecoON().pesquisarVAipCepsPorLogradouroCidade(descricao, codigoCidade);
	}

	public VAipCeps pesquisarCepUnicoPorCidade(Integer codigoCidade)throws ApplicationBusinessException {
		return this.getEnderecoON().pesquisarVAipCepUnicoPorCidade(codigoCidade);
	}

	@Override
	public AipBairrosCepLogradouro obterBairroCepLogradouroPorCepBairroLogradouro(
			Integer cep, Integer codigoBairroLogradouro,
			Integer codigoLogradouro) {
		return getAipBairrosCepLogradouroDAO().obterBairroCepLogradouroPorCepBairroLogradouro(cep, codigoBairroLogradouro, codigoLogradouro);
	}
	
	@Override
	public AipBairrosCepLogradouro obterBairroCepLogradouroPorCep(Integer cep){
		return getAipBairrosCepLogradouroDAO().obterBairroCepLogradouroPorCep(cep);
	}

	public AipBairrosCepLogradouroDAO getAipBairrosCepLogradouroDAO() {
		return aipBairrosCepLogradouroDAO;
	}
	
	@Override
	public boolean verificarProntuarioFamiliaVisivel(String nomeMicrocomputador) {
		return getCadastroPacienteON().verificarMicroAlteraProntuarioFamilia(nomeMicrocomputador);
	}
		
	@Override
	public List<AipCidades> pesquisarCidadesPorNome(String nome) {
		return this.aipCidadesDAO.pesquisarCidadesPorNome(nome);
	}
	@Override
	public AipBairrosCepLogradouro obterAipBairrosCepLogradouroPorId(AipBairrosCepLogradouroId id) {
		return aipBairrosCepLogradouroDAO.obterAipBairrosCepLogradouroPorId(id);
	}
	
	@Override
	public AipEnderecosPacientes obterEnderecoResidencialPadraoPaciente(AipPacientes paciente){
		return this.aipEnderecosPacientesDAO.obterEnderecoResidencialPadraoPaciente(paciente);
	}
	
	@Override
	public AipOrgaosEmissores obterOrgaoEmissorPorCodigo(Short codigo) {
		return this.aipOrgaosEmissoresDAO.obterPorChavePrimaria(codigo);
	}


}