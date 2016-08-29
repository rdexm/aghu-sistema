package br.gov.mec.aghu.internacao.transferir.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface ITransferirPacienteFacade extends Serializable {

	public List<AinLeitos> pesquisarLeitos(Object strPesq, Integer intSeq);

	public Date getDthrAltaMedica(Integer intSeq);

	public AinInternacao obterInternacao(Integer seq);

	public ProfessorCrmInternacaoVO obterProfessorCrmInternacaoVO(RapServidores servidorProfessor, AghEspecialidades especialidade,
			Short cnvCodigo);

	public void validarQuartoInexistente(String ainQuartosDescricao) throws ApplicationBusinessException;

	public AinQuartos obterQuarto(String ainQuartosDescricao);

	public void validarPacienteJaPossuiAlta(AghAtendimentos atendimento, Date dtTransferencia) throws ApplicationBusinessException;

	public void validarDestino(AinInternacao internacao) throws ApplicationBusinessException;

	public void validaEspecialidade(AinInternacao internacao) throws ApplicationBusinessException;

	public boolean validarDataTransferencia(Date dataTransf) throws ApplicationBusinessException;

	public void validarDataTransferenciaPosteriorAlta(AinInternacao internacao) throws ApplicationBusinessException;

	public void validarDataInternacao(AinInternacao internacao) throws ApplicationBusinessException;

	public boolean consisteClinicaEEspecialidade(AinInternacao internacao) throws ApplicationBusinessException;

	public void atualizarInternacao(AinInternacao internacao, AinInternacao oldInternacao, String nomeMicrocomputador) throws BaseException;

	/**
	 * Melhoria #8745 Considerar parâmetro de horas para impressão de Controles
	 * na transferência do paciente
	 * 
	 * @param internacaoSeq
	 * @param unidadeSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date buscarDataInicioRelatorio(Integer internacaoSeq, Short unidadeSeq) throws ApplicationBusinessException;

	public boolean deveImprimirControlesPaciente(Short unidadeOrigemSeq, Short unidadeDestinoSeq);

	public List<ProfessorCrmInternacaoVO> pesquisarProfessor(String strPesq, AghEspecialidades especialidade, AinInternacao internacao);

	public List<AghEspecialidades> pesquisarEspecialidades(Object strPesquisa, Integer idadePaciente);

	public List<AghUnidadesFuncionais> pesquisarUnidades(Object strPesquisa);

	public List<AinInternacao> pesquisarInternacao(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer prontuario);

	public Long pesquisarInternacaoCount(Integer prontuario);

	String atualizarSolicitacaoTransferencia(Integer prontuario, String leitoID, Boolean confirmacaoUsuario)
			throws ApplicationBusinessException;

	List<AinLeitos> buscarLeito(String leitoID);

	AinLeitos pesquisarApenasLeitoConcedido(Object strPesq, Integer intSeq);

	


}