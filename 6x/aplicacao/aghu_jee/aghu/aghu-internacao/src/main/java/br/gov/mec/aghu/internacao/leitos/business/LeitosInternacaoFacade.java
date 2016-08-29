package br.gov.mec.aghu.internacao.leitos.business;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.hibernate.Hibernate;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoTransferencia;
import br.gov.mec.aghu.internacao.leitos.vo.LiberaLeitoLimpezaVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.RapServidoresTransPacienteVO;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapServidores;

/**
 * Porta de entrada do sub-módulo Leitos do módulo de Internação.
 * 
 * @author lmoura
 * 
 */

@Modulo(ModuloEnum.INTERNACAO)
@Stateless
public class LeitosInternacaoFacade extends BaseFacade implements ILeitosInternacaoFacade {

	@EJB
	private AtenderTransferenciaPacienteON atenderTransferenciaPacienteON;

	@EJB
	private ExtratoLeitoRN extratoLeitoRN;

	@EJB
	private SolicitarTransferenciaPacienteON solicitarTransferenciaPacienteON;

	@EJB
	private ExtratoLeitoON extratoLeitoON;

	@EJB
	private LiberaLeitoLimpezaON liberaLeitoLimpezaON;

	private static final long serialVersionUID = -6383001982810441264L;

	/**
	 * Método para verificar se uma unidade funcional tem uma determinada
	 * característica.
	 * 
	 * @param seq
	 *            Unidade Funcional
	 * @param caracteristica
	 * @return true/false
	 */
	@Override
	public Boolean verificarCaracteristicaUnidadeFuncional(Short seq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return getExtratoLeitoRN().verificarCaracteristicaUnidadeFuncional(seq, caracteristica);
	}

	/**
	 * Atualiza leito
	 * 
	 * @param leito
	 * @param tiposMovimentoLeito
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void atualizarLeito(AinLeitos leito, AinTiposMovimentoLeito tiposMovimentoLeito, AinInternacao internacao)
			throws ApplicationBusinessException {
		getExtratoLeitoRN().atualizarLeito(leito, tiposMovimentoLeito, internacao);
	}

	/**
	 * Atualiza o leito gerando o novo extrato.
	 * 
	 * @param leito
	 * @param tiposMovimentoLeito
	 * @param rapServidor
	 * @param rapServidorResponsavel
	 * @param justificativa
	 * @param dataLancamento
	 * @param paciente
	 */
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('leito','alterarExtrato')}")
	public void inserirExtrato(AinLeitos leito, AinTiposMovimentoLeito tiposMovimentoLeito, RapServidores rapServidor,
			RapServidores rapServidorResponsavel, String justificativa, Date dataLancamento, Date criadoEm, AipPacientes paciente,
			AinInternacao internacao, Short tempoReserva, AinAtendimentosUrgencia atendimentoUrgencia, AghOrigemEventos origemEventos)
			throws ApplicationBusinessException {

		getExtratoLeitoON().inserirExtrato(leito, tiposMovimentoLeito, rapServidor, rapServidorResponsavel, justificativa, dataLancamento,
				criadoEm, paciente, internacao, tempoReserva, atendimentoUrgencia, origemEventos);
	}

	/**
	 * Retorna rapServidores de acordo com a matricula ou nome passado por
	 * parametro
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param responsavel
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<RapServidores> pesquisarResponsaveis(Object responsavel) {
		return getExtratoLeitoON().pesquisarResponsaveis(responsavel);
	}

	/**
	 * Valida os dados informados pelo usuário em Bloquear Leito
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param codigoLeito
	 * @param codigoStatus
	 * @param codigoResponsavel
	 * @param dataLancamento
	 * @param justificativa
	 *            Adicionado bypass issue #36419
	 */
	@Override
	@BypassInactiveModule
	public void validarDadosBloqueio(String codigoLeito, Integer codigoStatus, RapServidores rapServidores, Date dataLancamento,
			String justificativa, AinTiposMovimentoLeito tiposMovimentoLeito, AinLeitos leito) throws ApplicationBusinessException {
		getExtratoLeitoON().validarDadosBloqueio(codigoLeito, codigoStatus, rapServidores, dataLancamento, justificativa,
				tiposMovimentoLeito, leito);
	}

	/**
	 * Método que remove os extratos leitos de uma internação
	 * 
	 * @param extratosLeitos
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('leito','excluirExtrato')}")
	public void removerExtratosLeitos(Set<AinExtratoLeitos> extratosLeitos) throws ApplicationBusinessException {
		getExtratoLeitoON().removerExtratosLeitos(extratosLeitos);
	}

	/**
	 * Valida os dados informados pelo usuário em Liberar Leito
	 * 
	 * @param codigoLeito
	 * @param codigoStatus
	 * @param codigoResponsavel
	 * @param dataLancamento
	 * @param justificativa
	 */
	@Override
	@BypassInactiveModule
	public void validarDadosLiberacao(AinLeitos codigoLeito, AinTiposMovimentoLeito codigoStatus, RapServidores rapServidores,
			Date dataLancamento, String justificativa) throws ApplicationBusinessException {
		getExtratoLeitoON().validarDadosLiberacao(codigoLeito, codigoStatus, rapServidores, dataLancamento, justificativa);
	}

	/**
	 * Verifica se o leito necessita de limpeza
	 * 
	 * @param leito
	 */
	@Override
	public boolean verificarLimpeza(AinLeitos leito) {
		return getExtratoLeitoON().verificarLimpeza(leito);
	}

	/**
	 * Valida os dados informados pelo usuário em Liberar Leito
	 * 
	 * @param codigoLeito
	 * @param codigoTipoReserva
	 * @param codigoResponsavel
	 * @param matriculaResponsavel
	 * @param dataLancamento
	 * @param justificativa
	 * @param tiposMovimentoLeito
	 * @param leito
	 * @param codigoProntuario
	 * @param paciente
	 */
	@Override
	@BypassInactiveModule
	public void validarDadosReserva(AinLeitos codigoLeito, AinTiposMovimentoLeito codigoTipoReserva, RapServidores codigoResponsavel,
			RapServidores matriculaResponsavel, RapServidores rapServidores, Date dataLancamento, String justificativa,
			AinTiposMovimentoLeito tiposMovimentoLeito, AinLeitos leito, Integer codigoProntuario, AipPacientes paciente,
			Integer codigoOrigem, AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		getExtratoLeitoON().validarDadosReserva(codigoLeito, codigoTipoReserva, codigoResponsavel, matriculaResponsavel, rapServidores,
				dataLancamento, justificativa, tiposMovimentoLeito, leito, codigoProntuario, paciente, codigoOrigem, origemEventos);

	}

	/**
	 * Método que obtém o count da lista de leitos limpeza.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * 
	 * @param leito
	 * @return
	 */
	@Override
	public Long pesquisarLeitosLimpezaCount(String leito) {
		return getLiberaLeitoLimpezaON().pesquisarLeitosLimpezaCount(leito);
	}

	/**
	 * Método que obtém a lista de leitos limpeza.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<LiberaLeitoLimpezaVO> pesquisarLeitosLimpeza(String leito, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getLiberaLeitoLimpezaON().pesquisarLeitosLimpeza(leito, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método responsável pela liberação do leito.
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('leito','liberarLimpeza')}")
	public void liberarLeitoLimpeza(AinLeitos leito) throws ApplicationBusinessException {
		this.getLiberaLeitoLimpezaON().liberarLeitoLimpeza(leito);
	}

	/**
	 * Método responsável pelo bloqueio do leito. TODO: Este método é restrito
	 * apenas a usuarios com perfil AING_SUPERV_HIGIEN_LEITO permissão para
	 * pesquisa. Lembrar de incluir esse mapeamento de segurança.
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('leito','bloquearLimpeza')}")
	public void bloquearLeitoLimpeza(AinLeitos leito, AinInternacao internacao) throws ApplicationBusinessException {
		this.getLiberaLeitoLimpezaON().bloquearLeitoLimpeza(leito, internacao);
	}

	/**
	 * Método que obtém a lista de leitos limpeza para geração do relatório.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	@Override
	public List<LiberaLeitoLimpezaVO> pesquisarLeitosLimpeza() {
		return this.getLiberaLeitoLimpezaON().pesquisarLeitosLimpeza();
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','pesquisar')}")
	public AinSolicTransfPacientes obterSolicTransfPacientePorId(Integer seq) {
		AinSolicTransfPacientes ainSolicTransfPacientes = getSolicitarTransferenciaPacienteON().obterSolicTransfPacientePorId(seq);

		Hibernate.initialize(ainSolicTransfPacientes.getInternacao());
		if (ainSolicTransfPacientes.getInternacao() != null) {
			inicializarAtributosInternacao(ainSolicTransfPacientes.getInternacao());
		}
		Hibernate.initialize(ainSolicTransfPacientes.getServidorProfessor());
		if (ainSolicTransfPacientes.getServidorProfessor() != null) {
			Hibernate.initialize(ainSolicTransfPacientes.getServidorProfessor().getPessoaFisica());
		}
		Hibernate.initialize(ainSolicTransfPacientes.getAcomodacoes());
		Hibernate.initialize(ainSolicTransfPacientes.getServidorSolicitante());
		if (ainSolicTransfPacientes.getServidorSolicitante() != null) {
			Hibernate.initialize(ainSolicTransfPacientes.getServidorSolicitante().getPessoaFisica());
		}
		Hibernate.initialize(ainSolicTransfPacientes.getEspecialidades());
		Hibernate.initialize(ainSolicTransfPacientes.getUnfSolicitante());

		return ainSolicTransfPacientes;
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoPorId(Integer seq) {
		AinInternacao ainInternacao = getSolicitarTransferenciaPacienteON().obterInternacaoPorId(seq);
		inicializarAtributosInternacao(ainInternacao);
		return ainInternacao;
	}

	private void inicializarAtributosInternacao(AinInternacao internacao) {
		Hibernate.initialize(internacao.getEspecialidade());
		if (internacao.getEspecialidade() != null) {
			Hibernate.initialize(internacao.getEspecialidade().getClinica());
		}
		Hibernate.initialize(internacao.getServidorProfessor());
		if (internacao.getServidorProfessor() != null) {
			Hibernate.initialize(internacao.getServidorProfessor().getPessoaFisica());
			if (internacao.getServidorProfessor().getPessoaFisica() != null) {
				Hibernate.initialize(internacao.getServidorProfessor().getPessoaFisica().getQualificacoes());
			}
		}
		Hibernate.initialize(internacao.getPaciente());
		Hibernate.initialize(internacao.getLeito());
		if (internacao.getLeito() != null) {
			Hibernate.initialize(internacao.getLeito().getQuarto().getClinica());
		}
		Hibernate.initialize(internacao.getQuarto());
		Hibernate.initialize(internacao.getUnidadesFuncionais());
		Hibernate.initialize(internacao.getConvenioSaude());
		Hibernate.initialize(internacao.getConvenioSaudePlano());
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public AghUnidadesFuncionais unidadeFuncionalInternacao(AinInternacao internacao) {
		return getSolicitarTransferenciaPacienteON().unidadeFuncionalInternacao(internacao);
	}

	/**
	 * Método responsável pelo cancekamento de uma solicitação de transferência
	 * de paciente.
	 * 
	 * @param solicitacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','cancelar')}")
	public void cancelarAinSolicTransfPacientes(AinSolicTransfPacientes solicitacao) throws ApplicationBusinessException {
		getSolicitarTransferenciaPacienteON().cancelar(solicitacao);
	}

	/**
	 * Método responsável pela persistência de uma solicitação de transferência
	 * de paciente.
	 * 
	 * @param solicitacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','alterar')}")
	public void persistirAinSolicTransfPacientes(AinSolicTransfPacientes solicitacao) throws ApplicationBusinessException {
		getSolicitarTransferenciaPacienteON().persistir(solicitacao);
	}

	@Override
	public List<RapServidoresTransPacienteVO> pesquisarServidoresPorNomeOuCRM(String paramPesquisa, AghEspecialidades especialidade,
			FatConvenioSaude convenio) throws ApplicationBusinessException {
		return getSolicitarTransferenciaPacienteON().pesquisarServidoresPorNomeOuCRM(paramPesquisa, especialidade, convenio);
	}

	/**
	 * Usado na LOV classica para buscar uma lista de RapServidores pela
	 * matricula ou nome;
	 * 
	 * @param responsavel
	 * @return
	 */
	@Override
	public List<RapServidores> pesquisarSolicitantes(Object responsavel) {
		return getSolicitarTransferenciaPacienteON().pesquisarSolicitantes(responsavel);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(Object parametro) {
		return getSolicitarTransferenciaPacienteON().pesquisarUnidadeFuncional(parametro);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoPorProntuario(Integer prontuario) {
		return getSolicitarTransferenciaPacienteON().obterInternacaoPorProntuario(prontuario);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoPorLeito(String leitoId) {
		return getSolicitarTransferenciaPacienteON().obterInternacaoPorLeito(leitoId);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','pesquisar')}")
	public String mensagemSolicTransPaciente(Integer internacao) {
		return getSolicitarTransferenciaPacienteON().mensagemSolicTransPaciente(internacao);
	}

	@Override
	public Long pesquisarSolicitacaoTransferenciaLeitoCount(Integer prontuario, String leitoID) {
		return getSolicitarTransferenciaPacienteON().pesquisarSolicitacaoTransferenciaLeitoCount(prontuario, leitoID);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','pesquisar')}")
	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaLeito(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer prontuario, String leitoID) throws ApplicationBusinessException {
		List<AinSolicTransfPacientes> lista = getSolicitarTransferenciaPacienteON().pesquisarSolicitacaoTransferenciaLeito(firstResult,
				maxResults, orderProperty, asc, prontuario, leitoID);
		for (AinSolicTransfPacientes ainSolicTransfPacientes : lista) {
			Hibernate.initialize(ainSolicTransfPacientes.getAcomodacoes());
			Hibernate.initialize(ainSolicTransfPacientes.getEspecialidades());
			Hibernate.initialize(ainSolicTransfPacientes.getServidorProfessor());
			if (ainSolicTransfPacientes.getServidorProfessor() != null) {
				Hibernate.initialize(ainSolicTransfPacientes.getServidorProfessor().getPessoaFisica());
			}
			Hibernate.initialize(ainSolicTransfPacientes.getInternacao());
			if (ainSolicTransfPacientes.getInternacao() != null) {
				inicializarAtributosInternacao(ainSolicTransfPacientes.getInternacao());
			}
		}
		return lista;
	}

	/**
	 * @param matricula
	 * @param vinCodigo
	 * @return List<EspCrmVO>, contendo os dados do medico (CRM, nomeMedico,
	 *         nomeUsual, espSeq, cpf), onde cada elemento da lista armazena uma
	 *         especialidade do medico.
	 */
	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<EspCrmVO> obterDadosDoMedicoPelaMatriculaEVinCodigo(Integer matricula, Short vinCodigo) {
		return getSolicitarTransferenciaPacienteON().obterDadosDoMedicoPelaMatriculaEVinCodigo(matricula, vinCodigo);
	}

	/**
	 * Método que deleta as solicitações de transferência de uma internação
	 * 
	 * @param solicitacoesTransf
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','excluir')}")
	public void removerSolicitacoesTransferenciaPaciente(Set<AinSolicTransfPacientes> solicitacoesTransf)
			throws ApplicationBusinessException {
		getSolicitarTransferenciaPacienteON().removerSolicitacoesTransferenciaPaciente(solicitacoesTransf);
	}

	@Override
	public List<RapServidores> pesquisarServidores(String paramPesquisa, AghEspecialidades especialidade, FatConvenioSaude convenio)
			throws ApplicationBusinessException {
		return getSolicitarTransferenciaPacienteON().pesquisarServidores(paramPesquisa, especialidade, convenio);
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitosDisponiveis(String pesquisa, AinSolicTransfPacientes solicitacao) {
		return getAtenderTransferenciaPacienteON().pesquisarLeitosDisponiveis(pesquisa, solicitacao);
	}

	@Override
	@Secure("#{s:hasPermission('transferenciaPaciente','pesquisar')}")
	public AinSolicTransfPacientes obterSolicitacaoPorSeq(Integer seq) {
		return getAtenderTransferenciaPacienteON().obterSolicitacaoPorSeq(seq);
	}

	@Override
	public Long pesquisarSolicitacaoTransferenciaPacienteCount(DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao,
			Date criadoEm, Integer prontuario, AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm, AghEspecialidades especialidade) {
		return getAtenderTransferenciaPacienteON().pesquisarSolicitacaoTransferenciaPacienteCount(indSolicitacaoInternacao, criadoEm,
				prontuario, unidadeFuncional, crm, especialidade);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public String recuperaUnidadeFuncionalInternacao(Integer seqInternacao) {
		return this.getAtenderTransferenciaPacienteON().recuperaUnidadeFuncionalInternacao(seqInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('transferenciaPaciente','pesquisar')}")
	public Boolean verificaSolicitacoesComAlerta(AinSolicTransfPacientes solicitacao) {
		return this.getAtenderTransferenciaPacienteON().verificaSolicitacoesComAlerta(solicitacao);
	}

	@Override
	@Secure("#{s:hasPermission('transferenciaPaciente','pesquisar')}")
	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPaciente(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao, Date criadoEm,
			Integer prontuario, AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm, AghEspecialidades especialidade) {
		return this.getAtenderTransferenciaPacienteON().pesquisarSolicitacaoTransferenciaPaciente(firstResult, maxResults, orderProperty,
				asc, indSolicitacaoInternacao, criadoEm, prontuario, unidadeFuncional, crm, especialidade);
	}

	@Override
	@Secure("#{s:hasPermission('transferenciaPaciente','cancelar')}")
	public void cancelarSolicitacao(AinSolicTransfPacientes solicitacao) throws ApplicationBusinessException {
		this.getAtenderTransferenciaPacienteON().cancelarSolicitacao(solicitacao);
	}

	@Override
	@Secure("#{s:hasPermission('transferenciaPaciente','atender')}")
	public void atenderSolicitacaoTransferencia(AinSolicTransfPacientes solicitacao, AinLeitos leitoConcedido)
			throws ApplicationBusinessException {
		this.getAtenderTransferenciaPacienteON().atenderSolicitacaoTransferencia(solicitacao, leitoConcedido);
	}

	protected ExtratoLeitoRN getExtratoLeitoRN() {
		return extratoLeitoRN;
	}

	protected ExtratoLeitoON getExtratoLeitoON() {
		return extratoLeitoON;
	}

	protected LiberaLeitoLimpezaON getLiberaLeitoLimpezaON() {
		return liberaLeitoLimpezaON;
	}

	protected SolicitarTransferenciaPacienteON getSolicitarTransferenciaPacienteON() {
		return solicitarTransferenciaPacienteON;
	}

	protected AtenderTransferenciaPacienteON getAtenderTransferenciaPacienteON() {
		return atenderTransferenciaPacienteON;
	}

	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPorProntuario(Integer prontuario) {
		return getAtenderTransferenciaPacienteON().pesquisarSolicitacaoTransferenciaPorProntuario(prontuario);
	}

}
