package br.gov.mec.aghu.internacao.solicitacao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
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

/**
 * Porta de entrada do sub-módulo Solicitacao do módulo de Internação.
 * 
 * @author lcmoura
 * 
 */


@Modulo(ModuloEnum.INTERNACAO)
@Stateless
public class SolicitacaoInternacaoFacade extends BaseFacade implements ISolicitacaoInternacaoFacade {
	
	@EJB
	private SolicitacaoInternacaoON solicitacaoInternacaoON;
	
	@EJB
	private SolicitacaoInternacaoRN solicitacaoInternacaoRN;
	
	private static final long serialVersionUID = -3935156387830026747L;

	/**
	 * Este método implementa a view descrita acima, porém trazendo apenas o
	 * campo CGI.CPG_CPH_CSP_CNV_CODIGO, necessário na pesquisa de procedimentos
	 * de internação quando um CID já tiver sido informado.
	 * 
	 * @param cidSeq
	 * @return listaCodTabelas
	 */
	@Override
	public List<Long> pesquisarFatAssociacaoProcedimentos(Integer cidSeq) {
		return getSolicitacaoInternacaoRN().pesquisarFatAssociacaoProcedimentos(cidSeq);
	}

	@Override
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVO(Integer firstResult, Integer maxResult, String strPesquisa) {
		return getSolicitacaoInternacaoRN().pesquisarConvenioPlanoVO(firstResult, maxResult, strPesquisa);
	}

	@Override
	public ConvenioPlanoVO obterConvenioPlanoVO(Short cnvCodigo, Byte seq) {
		return getSolicitacaoInternacaoRN().obterConvenioPlanoVO(cnvCodigo, seq);
	}

	/**
	 * Obtém o convênio e o plano que permite internação
	 * 
	 * @param cnvCodigo
	 * @return
	 */
	@Override
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(Short cnvCodigo) {
		return getSolicitacaoInternacaoRN().obterConvenioPlanoInternacao(cnvCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public AinSolicitacoesInternacao obterAinSolicitacoesInternacao(Integer seq) {
		return getSolicitacaoInternacaoON().obterSolicitacaoInternacaoDetalhada(seq);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoInternacao','atender')}")
	public void atenderSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		getSolicitacaoInternacaoON().atenderSolicitacaoInternacao(solicitacaoInternacao, substituirProntuario);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoInternacao','alterar')}")
	public void persistirSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		getSolicitacaoInternacaoON().persistirSolicitacaoInternacao(
				solicitacaoInternacao, solicitacaoInternacaoTemp, substituirProntuario);
	}

	@Override
	public List<ServidoresCRMVO> pesquisarServidorCRMVOPorNomeeCRM(Object strPesquisa) {
		return getSolicitacaoInternacaoON().pesquisarServidorCRMVOPorNomeeCRM(strPesquisa);
	}

	/**
	 * Retorna a lista de FatItensProcedHospitalar
	 * 
	 * @param descricao
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('procedimentoHospitalar','pesquisar')}")
	public List<FatItensProcedHospitalar> pesquisarFatItensProcedHospitalar(Object descricao, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().pesquisarFatItensProcedHospitalar(descricao, paciente, cidSeq);
	}

	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVOPorCodigoDescricao(Object strPesquisa) {
		return getSolicitacaoInternacaoON().pesquisarConvenioPlanoVOPorCodigoDescricao(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public EspCrmVO obterCrmENomeMedicoPorServidor(RapServidores servidor, AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().obterCrmENomeMedicoPorServidor(servidor, especialidade);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public List<EspCrmVO> pesquisarEspCrmVO(Object nomeMedico, AghEspecialidades especialidade) throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().pesquisarEspCrmVO(nomeMedico, especialidade);
	}

	@Override
	public EspCrmVO obterEspCrmVOComAmbulatorio(Object nomeMedico, AghEspecialidades especialidade, DominioSimNao ambulatorio,
			RapServidores servidor) throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().obterEspCrmVOComAmbulatorio(nomeMedico, especialidade, ambulatorio, servidor);
	}

	@Override
	public Long pesquisarSolicitacaoInternacaoCount(DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao,
			AghClinicas clinica, Date criadoEm, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio) throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().pesquisarSolicitacaoInternacaoCount(indSolicitacaoInternacao, clinica, criadoEm,
				dtPrevistaInternacao, paciente, crm, especialidade, convenio);
	}

	@Override
	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public List<AinSolicitacoesInternacao> pesquisarSolicitacaoInternacao(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao, AghClinicas clinica,
			Date criadoEm, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm, AghEspecialidades especialidade,
			ConvenioPlanoVO convenio) throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().pesquisarSolicitacaoInternacao(firstResult, maxResults, orderProperty, asc,
				indSolicitacaoInternacao, clinica, criadoEm, dtPrevistaInternacao, paciente, crm, especialidade, convenio);
	}
	
	@Override
	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public AinSolicitacoesInternacao obterSolicitacaoInternacao(Integer seq) {
		return getSolicitacaoInternacaoON().obterSolicitacaoInternacao(seq);		
	}

	@Override
	public AinSolicitacoesInternacao obterSolicitacaoInternacaoDetached(AinSolicitacoesInternacao solicitacaoInternacao) {
		return getSolicitacaoInternacaoON().obterSolicitacaoInternacaoDetached(solicitacaoInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<ServidorConselhoVO> pesquisarServidorConselhoVOPorNomeeCRM(Object strPesquisa) {
		return getSolicitacaoInternacaoON().pesquisarServidorConselhoVOPorNomeeCRM(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public ProfessorVO pesquisarProfessorVO(RapServidoresId idProfessor) {
		return getSolicitacaoInternacaoON().pesquisarProfessorVO(idProfessor);
	}

	@Override
	@Secure("#{s:hasPermission('procedimentoHospitalar','pesquisar')}")
	public List<FatVlrItemProcedHospComps> pesquisarFatVlrItemProcedHospComps(
			Object descricao, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().pesquisarFatVlrItemProcedHospComps(descricao, paciente, cidSeq);
	}
	
	@Override
	public void validarCrmEspecialidade(String nomeMedico, AghEspecialidades especialidade) throws ApplicationBusinessException {
		getSolicitacaoInternacaoON().validarCrmEspecialidade(nomeMedico, especialidade);
	}
	
	@Override
	public AinSolicitacoesInternacao obterPrimeiraSolicitacaoPendentePorPaciente(Integer codigoPaciente) throws ApplicationBusinessException {
		return getSolicitacaoInternacaoON().obterPrimeiraSolicitacaoPendentePorPaciente(codigoPaciente);
	}

	/*
	 * ### GETS E SETS ###
	 */
	protected SolicitacaoInternacaoON getSolicitacaoInternacaoON() {
		return solicitacaoInternacaoON;
	}

	protected SolicitacaoInternacaoRN getSolicitacaoInternacaoRN() {
		return solicitacaoInternacaoRN;
	}
	
	@Override
	public void atualizarSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp,
			Boolean substituirProntuario) throws ApplicationBusinessException {
		getSolicitacaoInternacaoON().atualizarSolicitacaoInternacao(
				solicitacaoInternacao, solicitacaoInternacaoTemp,
				substituirProntuario);
	}
}
