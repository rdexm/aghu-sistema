package br.gov.mec.aghu.internacao.solicitacao.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.ProfessorVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ISolicitacaoInternacaoFacade extends Serializable {

	/**
	 * Este método implementa a view descrita acima, porém trazendo apenas o
	 * campo CGI.CPG_CPH_CSP_CNV_CODIGO, necessário na pesquisa de procedimentos
	 * de internação quando um CID já tiver sido informado.
	 * 
	 * @param cidSeq
	 * @return listaCodTabelas
	 */
	public List<Long> pesquisarFatAssociacaoProcedimentos(Integer cidSeq);

	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVO(Integer firstResult,
			Integer maxResult, String strPesquisa);

	public ConvenioPlanoVO obterConvenioPlanoVO(Short cnvCodigo, Byte seq);

	/**
	 * Obtém o convênio e o plano que permite internação
	 * 
	 * @param cnvCodigo
	 * @return
	 */
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(Short cnvCodigo);

	
	public AinSolicitacoesInternacao obterAinSolicitacoesInternacao(Integer seq);

	
	public void atenderSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao, Boolean substituirProntuario)
			throws ApplicationBusinessException;

	
	public void persistirSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp, Boolean substituirProntuario)
			throws ApplicationBusinessException;

	public List<ServidoresCRMVO> pesquisarServidorCRMVOPorNomeeCRM(
			Object strPesquisa);

	/**
	 * Retorna a lista de FatItensProcedHospitalar
	 * 
	 * @param descricao
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	
	public List<FatItensProcedHospitalar> pesquisarFatItensProcedHospitalar(
			Object descricao, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException;

	
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVOPorCodigoDescricao(
			Object strPesquisa);

	
	public EspCrmVO obterCrmENomeMedicoPorServidor(RapServidores servidor,
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	
	public List<EspCrmVO> pesquisarEspCrmVO(Object nomeMedico,
			AghEspecialidades especialidade) throws ApplicationBusinessException;

	public EspCrmVO obterEspCrmVOComAmbulatorio(Object nomeMedico,
			AghEspecialidades especialidade, DominioSimNao ambulatorio,
			RapServidores servidor) throws ApplicationBusinessException;

	public Long pesquisarSolicitacaoInternacaoCount(
			DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao,
			AghClinicas clinica, Date criadoEm, Date dtPrevistaInternacao,
			AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio)
			throws ApplicationBusinessException;

	
	public List<AinSolicitacoesInternacao> pesquisarSolicitacaoInternacao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc,
			DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao,
			AghClinicas clinica, Date criadoEm, Date dtPrevistaInternacao,
			AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio)
			throws ApplicationBusinessException;

	
	public AinSolicitacoesInternacao obterSolicitacaoInternacao(Integer seq);

	public AinSolicitacoesInternacao obterSolicitacaoInternacaoDetached(
			AinSolicitacoesInternacao solicitacaoInternacao);

	
	public List<ServidorConselhoVO> pesquisarServidorConselhoVOPorNomeeCRM(
			Object strPesquisa);

	
	public ProfessorVO pesquisarProfessorVO(RapServidoresId idProfessor);

	
	public List<FatVlrItemProcedHospComps> pesquisarFatVlrItemProcedHospComps(
			Object descricao, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException;

	public void validarCrmEspecialidade(String nomeMedico,
			AghEspecialidades especialidade) throws ApplicationBusinessException;
	
	void atualizarSolicitacaoInternacao(AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp, Boolean substituirProntuario) throws ApplicationBusinessException;

	AinSolicitacoesInternacao obterPrimeiraSolicitacaoPendentePorPaciente(
			Integer codigoPaciente) throws ApplicationBusinessException;

}