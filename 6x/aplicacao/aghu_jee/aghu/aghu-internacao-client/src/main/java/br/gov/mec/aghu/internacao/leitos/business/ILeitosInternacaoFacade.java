package br.gov.mec.aghu.internacao.leitos.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ILeitosInternacaoFacade extends Serializable {

	/**
	 * Método para verificar se uma unidade funcional tem uma determinada
	 * característica.
	 * 
	 * @param seq
	 *            Unidade Funcional
	 * @param caracteristica
	 * @return true/false
	 */
	public Boolean verificarCaracteristicaUnidadeFuncional(Short seq, ConstanteAghCaractUnidFuncionais caracteristica);

	/**
	 * Atualiza leito
	 * 
	 * @param leito
	 * @param tiposMovimentoLeito
	 * @throws ApplicationBusinessException
	 */
	public void atualizarLeito(AinLeitos leito, AinTiposMovimentoLeito tiposMovimentoLeito, AinInternacao internacao)
			throws ApplicationBusinessException;

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
	public void inserirExtrato(AinLeitos leito, AinTiposMovimentoLeito tiposMovimentoLeito, RapServidores rapServidor,
			RapServidores rapServidorResponsavel, String justificativa, Date dataLancamento, Date criadoEm, AipPacientes paciente,
			AinInternacao internacao, Short tempoReserva, AinAtendimentosUrgencia atendimentoUrgencia, AghOrigemEventos origemEventos)
			throws ApplicationBusinessException;

	/**
	 * Retorna rapServidores de acordo com a matricula ou nome passado por
	 * parametro
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param responsavel
	 * @return
	 */

	public List<RapServidores> pesquisarResponsaveis(Object responsavel);

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
	 */
	public void validarDadosBloqueio(String codigoLeito, Integer codigoStatus, RapServidores rapServidores, Date dataLancamento,
			String justificativa, AinTiposMovimentoLeito tiposMovimentoLeito, AinLeitos leito) throws ApplicationBusinessException;

	/**
	 * Método que remove os extratos leitos de uma internação
	 * 
	 * @param extratosLeitos
	 * @throws ApplicationBusinessException
	 */

	public void removerExtratosLeitos(Set<AinExtratoLeitos> extratosLeitos) throws ApplicationBusinessException;

	/**
	 * Valida os dados informados pelo usuário em Liberar Leito
	 * 
	 * @param codigoLeito
	 * @param codigoStatus
	 * @param codigoResponsavel
	 * @param dataLancamento
	 * @param justificativa
	 */
	public void validarDadosLiberacao(AinLeitos codigoLeito, AinTiposMovimentoLeito codigoStatus, RapServidores rapServidores,
			Date dataLancamento, String justificativa) throws ApplicationBusinessException;

	/**
	 * Verifica se o leito necessita de limpeza
	 * 
	 * @param leito
	 */
	public boolean verificarLimpeza(AinLeitos leito);

	/**
	 * Valida os dados informados pelo usuário em Liberar Leito
	 * 
	 * @param leitos
	 * @param tiposMovimentoLeito2
	 * @param rapServidores2
	 * @param rapServidores3
	 * @param dataLancamento
	 * @param justificativa
	 * @param tiposMovimentoLeito
	 * @param leito
	 * @param codigoProntuario
	 * @param paciente
	 */
	public void validarDadosReserva(AinLeitos leitos, AinTiposMovimentoLeito tiposMovimentoLeito2, RapServidores rapServidores2,
			RapServidores rapServidores3, RapServidores rapServidores, Date dataLancamento, String justificativa,
			AinTiposMovimentoLeito tiposMovimentoLeito, AinLeitos leito, Integer codigoProntuario, AipPacientes paciente,
			Integer codigoOrigem, AghOrigemEventos origemEventos) throws ApplicationBusinessException;

	/**
	 * Método que obtém o count da lista de leitos limpeza.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * 
	 * @param leito
	 * @return
	 */
	public Long pesquisarLeitosLimpezaCount(String leito);

	/**
	 * Método que obtém a lista de leitos limpeza.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */

	public List<LiberaLeitoLimpezaVO> pesquisarLeitosLimpeza(String leito, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	/**
	 * Método responsável pela liberação do leito.
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */

	public void liberarLeitoLimpeza(AinLeitos leito) throws ApplicationBusinessException;

	/**
	 * Método responsável pelo bloqueio do leito. TODO: Este método é restrito
	 * apenas a usuarios com perfil AING_SUPERV_HIGIEN_LEITO permissão para
	 * pesquisa. Lembrar de incluir esse mapeamento de segurança.
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */

	public void bloquearLeitoLimpeza(AinLeitos leito, AinInternacao internacao) throws ApplicationBusinessException;

	/**
	 * Método que obtém a lista de leitos limpeza para geração do relatório.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	public List<LiberaLeitoLimpezaVO> pesquisarLeitosLimpeza();

	public AinSolicTransfPacientes obterSolicTransfPacientePorId(Integer seq);

	public AinInternacao obterInternacaoPorId(Integer seq);

	public AghUnidadesFuncionais unidadeFuncionalInternacao(AinInternacao internacao);

	/**
	 * Método responsável pelo cancekamento de uma solicitação de transferência
	 * de paciente.
	 * 
	 * @param solicitacao
	 * @throws ApplicationBusinessException
	 */

	public void cancelarAinSolicTransfPacientes(AinSolicTransfPacientes solicitacao) throws ApplicationBusinessException;

	/**
	 * Método responsável pela persistência de uma solicitação de transferência
	 * de paciente.
	 * 
	 * @param solicitacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */

	public void persistirAinSolicTransfPacientes(AinSolicTransfPacientes solicitacao) throws ApplicationBusinessException;

	public List<RapServidoresTransPacienteVO> pesquisarServidoresPorNomeOuCRM(String paramPesquisa, AghEspecialidades especialidade,
			FatConvenioSaude convenio) throws ApplicationBusinessException;

	/**
	 * Usado na LOV classica para buscar uma lista de RapServidores pela
	 * matricula ou nome;
	 * 
	 * @param responsavel
	 * @return
	 */
	public List<RapServidores> pesquisarSolicitantes(Object responsavel);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(Object parametro);

	public AinInternacao obterInternacaoPorProntuario(Integer prontuario);

	public AinInternacao obterInternacaoPorLeito(String leitoId);

	public String mensagemSolicTransPaciente(Integer internacao);

	public Long pesquisarSolicitacaoTransferenciaLeitoCount(Integer prontuario, String leitoID);

	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaLeito(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer prontuario, String leitoID) throws ApplicationBusinessException;

	/**
	 * @param matricula
	 * @param vinCodigo
	 * @return List<EspCrmVO>, contendo os dados do medico (CRM, nomeMedico,
	 *         nomeUsual, espSeq, cpf), onde cada elemento da lista armazena uma
	 *         especialidade do medico.
	 */

	public List<EspCrmVO> obterDadosDoMedicoPelaMatriculaEVinCodigo(Integer matricula, Short vinCodigo);

	/**
	 * Método que deleta as solicitações de transferência de uma internação
	 * 
	 * @param solicitacoesTransf
	 * @throws ApplicationBusinessException
	 */

	public void removerSolicitacoesTransferenciaPaciente(Set<AinSolicTransfPacientes> solicitacoesTransf)
			throws ApplicationBusinessException;

	public List<RapServidores> pesquisarServidores(String paramPesquisa, AghEspecialidades especialidade, FatConvenioSaude convenio)
			throws ApplicationBusinessException;

	public List<AinLeitos> pesquisarLeitosDisponiveis(String pesquisa, AinSolicTransfPacientes solicitacao);

	public AinSolicTransfPacientes obterSolicitacaoPorSeq(Integer seq);

	public Long pesquisarSolicitacaoTransferenciaPacienteCount(DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao,
			Date criadoEm, Integer prontuario, AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm, AghEspecialidades especialidade);

	public String recuperaUnidadeFuncionalInternacao(Integer seqInternacao);

	public Boolean verificaSolicitacoesComAlerta(AinSolicTransfPacientes solicitacao);

	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPaciente(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao, Date criadoEm,
			Integer prontuario, AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm, AghEspecialidades especialidade);

	public void cancelarSolicitacao(AinSolicTransfPacientes solicitacao) throws ApplicationBusinessException;

	public void atenderSolicitacaoTransferencia(AinSolicTransfPacientes solicitacao, AinLeitos leitoConcedido)
			throws ApplicationBusinessException;

	List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPorProntuario(Integer prontuario);

}