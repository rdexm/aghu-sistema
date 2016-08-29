package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmListaPacCpa;
import br.gov.mec.aghu.model.MpmListaPacCpaId;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.MpmListaServEquipeId;
import br.gov.mec.aghu.model.MpmListaServEspecialidade;
import br.gov.mec.aghu.model.MpmListaServEspecialidadeId;
import br.gov.mec.aghu.model.MpmListaServResponsavel;
import br.gov.mec.aghu.model.MpmListaServResponsavelId;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmPacAtendProfissionalId;
import br.gov.mec.aghu.model.MpmServidorUnidFuncional;
import br.gov.mec.aghu.model.MpmServidorUnidFuncionalId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaPacCpaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServEquipeDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServEspecialidadeDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServResponsavelDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPacAtendProfissionalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmServidorUnidFuncionalDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConfigurarListaPacientesON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConfigurarListaPacientesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

@Inject
private MpmListaServResponsavelDAO mpmListaServResponsavelDAO;

@Inject
private MpmPacAtendProfissionalDAO mpmPacAtendProfissionalDAO;

@Inject
private MpmListaServEquipeDAO mpmListaServEquipeDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private MpmListaPacCpaDAO mpmListaPacCpaDAO;

@Inject
private MpmServidorUnidFuncionalDAO mpmServidorUnidFuncionalDAO;

@Inject
private MpmListaServEspecialidadeDAO mpmListaServEspecialidadeDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5051610747001987932L;

	public enum ConfigurarListaPacientesONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SERVIDOR_INVALIDO;
	}

	public List<AghEspecialidades> getListaEspecialidades(RapServidores servidor)
			throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmListaServEspecialidadeDAO dao = getMpmListaServEspecialidadesDAO();
		List<AghEspecialidades> result = dao
				.pesquisarEspecialidadesPorServidor(servidor);
		if (result == null) {
			result = new ArrayList<AghEspecialidades>();
		}
		return result;
	}

	public List<AghEspecialidades> getListaEspecialidades(String parametro) {
		List<AghEspecialidades> result = getAghuFacade()
				.pesquisarPorSiglaIndices(parametro);
		return result;
	}

	@SuppressWarnings("unchecked")
	
	public void salvarListaEspecialidades(
			List<AghEspecialidades> listaEspecialidades, RapServidores servidor)
			throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmListaServEspecialidade associacao = null;
		MpmListaServEspecialidadeId idAssociacao = null;
		// Lista com os itens que vieram da tela
		List<MpmListaServEspecialidade> listaServidorEspecialidades = new ArrayList<MpmListaServEspecialidade>();
		for (AghEspecialidades aghEspecialidade : listaEspecialidades) {
			idAssociacao = new MpmListaServEspecialidadeId(servidor.getId()
					.getMatricula(), servidor.getId().getVinCodigo(),
					aghEspecialidade.getSeq());
			associacao = new MpmListaServEspecialidade(idAssociacao);
			associacao.setEspecialidade(aghEspecialidade);
			associacao.setServidor(servidor);
			listaServidorEspecialidades.add(associacao);
		}
		// carrega os dados atuais do banco
		MpmListaServEspecialidadeDAO dao = getMpmListaServEspecialidadesDAO();
		List<MpmListaServEspecialidade> listaServidorEspecialidadesFromDB = dao
				.pesquisarAssociacoesPorServidor(servidor);
		// lista das associações para incluir no banco
		List<MpmListaServEspecialidade> paraIncluir = ListUtils.subtract(
				listaServidorEspecialidades, listaServidorEspecialidadesFromDB);
		// chamada de inserção para cada uma
		for (MpmListaServEspecialidade mpmListaServEspecialidade : paraIncluir) {
			mpmListaServEspecialidade.setCriadoEm(new Date());
			dao.persistir(mpmListaServEspecialidade);
			dao.flush();
		}
		// lista das associações para excluir do banco
		List<MpmListaServEspecialidade> paraExcluir = ListUtils.subtract(
				listaServidorEspecialidadesFromDB, listaServidorEspecialidades);
		for (MpmListaServEspecialidade mpmListaServEspecialidade : paraExcluir) {
			dao.remover(mpmListaServEspecialidade);
			dao.flush();
		}
	}

	public List<AghEquipes> getListaEquipes(RapServidores servidor)
			throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmListaServEquipeDAO dao = getMpmListaServEquipesDAO();
		List<AghEquipes> result = dao.pesquisarEquipesPorServidor(servidor);
		if (result == null) {
			result = new ArrayList<AghEquipes>();
		}
		return result;
	}

	public List<ProfessorCrmInternacaoVO> getListaResponsaveis(RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		List<ProfessorCrmInternacaoVO> listaProfessorCrmInternacaoVO = new ArrayList<ProfessorCrmInternacaoVO>();
		// carrega os dados atuais do banco
		MpmListaServResponsavelDAO dao = getMpmListaServResponsavelDAO();
		List<MpmListaServResponsavel> listaServidorRespBD = dao.pesquisarAssociacoesPorServidor(servidor);

		if (listaServidorRespBD != null) {
			for  (MpmListaServResponsavel mpmListaServResponsavel : listaServidorRespBD) {
				List<ProfessorCrmInternacaoVO> listProfessores = getInternacaoFacade().pesquisarProfessoresCrm(null, mpmListaServResponsavel.getServidorResponsavel().getId().getMatricula(), mpmListaServResponsavel.getServidorResponsavel().getId().getVinCodigo());
				if (listProfessores != null && !listProfessores.isEmpty()) {
					ProfessorCrmInternacaoVO professorCrmInternacaoVO = listProfessores.get(0);
					listaProfessorCrmInternacaoVO.add(professorCrmInternacaoVO);
				}
			}
		}
		return listaProfessorCrmInternacaoVO;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	public List<AghEquipes> getListaEquipesPorEspecialidade(
			Object objPesquisa, AghEspecialidades especialidade, DominioSituacao situacao) throws ApplicationBusinessException {
		MpmListaServEquipeDAO dao = getMpmListaServEquipesDAO();
		return dao.pesquisarEquipesPorEspecialidade(objPesquisa,especialidade,situacao);

	}	
	
	public List<VRapServidorConselho> getListaProfissionaisPorEspecialidade(
			Object objPesquisa, AghEspecialidades especialidade) throws ApplicationBusinessException {
		MpmListaServEquipeDAO dao = getMpmListaServEquipesDAO();
		AghParametros paramCodMed = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_CONSELHO_MED);
		AghParametros paramCodOdont = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_CONSELHO_ODONT);
		Short codMed = null;
		if (paramCodMed.getVlrNumerico() != null){
			codMed = paramCodMed.getVlrNumerico().shortValue();
		}
		Short codOdont = null;
		if (paramCodOdont.getVlrNumerico() != null){
			codOdont = paramCodOdont.getVlrNumerico().shortValue();
		}
		Object[] cprCodigos = {codMed,codOdont};
		return dao.pesquisarProfissionaisPorEspecialidade(objPesquisa, especialidade, cprCodigos);

	}

	public List<AghEquipes> getListaEquipes(String paramString)
			throws ApplicationBusinessException {
		List<AghEquipes> result = getAghuFacade().getListaEquipesAtivas(paramString);
		if (result == null) {
			result = new ArrayList<AghEquipes>();
		}
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public void salvarListaEquipes(List<AghEquipes> listaEquipes,
			RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmListaServEquipe associacao = null;
		MpmListaServEquipeId idAssociacao = null;
		// Lista com os itens que vieram da tela
		List<MpmListaServEquipe> listaServidorEquipes = new ArrayList<MpmListaServEquipe>();
		for (AghEquipes aghEquipe : listaEquipes) {
			idAssociacao = new MpmListaServEquipeId(servidor.getId()
					.getMatricula(), servidor.getId().getVinCodigo(), aghEquipe
					.getSeq().shortValue());
			associacao = new MpmListaServEquipe(idAssociacao);
			associacao.setEquipe(aghEquipe);
			associacao.setServidor(servidor);
			listaServidorEquipes.add(associacao);
		}
		// carrega os dados atuais do banco
		MpmListaServEquipeDAO dao = getMpmListaServEquipesDAO();
		List<MpmListaServEquipe> listaServidorEquipesFromDB = dao
				.pesquisarAssociacoesPorServidor(servidor);
		// lista das associações para incluir no banco
		List<MpmListaServEquipe> paraIncluir = ListUtils.subtract(
				listaServidorEquipes, listaServidorEquipesFromDB);
		// chamada de inserção para cada uma
		for (MpmListaServEquipe mpmListaServEquipe : paraIncluir) {
			mpmListaServEquipe.setCriadoEm(new Date());
			dao.persistir(mpmListaServEquipe);
			dao.flush();
		}
		// lista das associações para excluir do banco
		List<MpmListaServEquipe> paraExcluir = ListUtils.subtract(
				listaServidorEquipesFromDB, listaServidorEquipes);
		for (MpmListaServEquipe mpmListaServEquipe : paraExcluir) {
			dao.remover(mpmListaServEquipe);
			dao.flush();
		}
	}

	public void salvarListaResponsaveis(List<ProfessorCrmInternacaoVO> listaResp, RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmListaServResponsavel associacao = null;
		MpmListaServResponsavelId idAssociacao = null;
		//		// Lista com os itens que vieram da tela
		List<MpmListaServResponsavel> listaServidorResp = new ArrayList<MpmListaServResponsavel>();
		for (ProfessorCrmInternacaoVO professorCrmInternacaoVO : listaResp) {
			idAssociacao = new MpmListaServResponsavelId(servidor.getId()
					.getMatricula(), servidor.getId().getVinCodigo(), professorCrmInternacaoVO.getSerMatricula(), professorCrmInternacaoVO.getSerVinCodigo());
			associacao = new MpmListaServResponsavel(idAssociacao);
			
			associacao.setServidor(getRegistroColaboradorFacade().buscaServidor(new RapServidoresId(servidor.getId().getMatricula(), servidor.getId().getVinCodigo())));
			associacao.setServidorResponsavel(getRegistroColaboradorFacade().buscaServidor(new RapServidoresId(professorCrmInternacaoVO.getSerMatricula(), professorCrmInternacaoVO.getSerVinCodigo())));
			
			listaServidorResp.add(associacao);
		}
		//		// carrega os dados atuais do banco
		MpmListaServResponsavelDAO dao = getMpmListaServResponsavelDAO();
		List<MpmListaServResponsavel> listaServidorRespBD = dao
		.pesquisarAssociacoesPorServidor(servidor);
		//		// lista das associações para incluir no banco
		List<MpmListaServResponsavel> paraIncluir = ListUtils.subtract(listaServidorResp, listaServidorRespBD);
		//		// chamada de inserção para cada uma
		for (MpmListaServResponsavel mpmListaServResponsavel : paraIncluir) {
			mpmListaServResponsavel.setCriadoEm(new Date());
			dao.persistir(mpmListaServResponsavel);
			dao.flush();
		}
		//		// lista das associações para excluir do banco
		List<MpmListaServResponsavel> paraExcluir = ListUtils.subtract(listaServidorRespBD, listaServidorResp);
		for (MpmListaServResponsavel mpmListaServResponsavel : paraExcluir) {
			dao.remover(mpmListaServResponsavel);
			dao.flush();
		}
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmServidorUnidFuncionalDAO dao = getMpmServidorUnidFuncionaisDAO();
		List<AghUnidadesFuncionais> result = dao
				.pesquisarUnidadesFuncionaisPorServidor(servidor);
		if (result == null) {
			result = new ArrayList<AghUnidadesFuncionais>();
		}
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public void salvarListaUnidadesFuncionais(
			List<AghUnidadesFuncionais> listaUnidadesFuncionais,
			RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmServidorUnidFuncional associacao = null;
		MpmServidorUnidFuncionalId idAssociacao = null;
		// Lista com os itens que vieram da tela
		List<MpmServidorUnidFuncional> listaServidorUnidadesFuncionais = new ArrayList<MpmServidorUnidFuncional>();
		for (AghUnidadesFuncionais unidadeFuncional : listaUnidadesFuncionais) {
			idAssociacao = new MpmServidorUnidFuncionalId(servidor.getId()
					.getMatricula(), servidor.getId().getVinCodigo(),
					unidadeFuncional.getSeq());
			associacao = new MpmServidorUnidFuncional(idAssociacao);
			associacao.setUnidadeFuncional(unidadeFuncional);
			associacao.setServidor(servidor);
			listaServidorUnidadesFuncionais.add(associacao);
		}
		// carrega os dados atuais do banco
		MpmServidorUnidFuncionalDAO dao = getMpmServidorUnidFuncionaisDAO();
		List<MpmServidorUnidFuncional> listaServidorUnidadesFuncionaisFromDB = dao
				.pesquisarAssociacoesPorServidor(servidor);
		// lista das associações para incluir no banco
		List<MpmServidorUnidFuncional> paraIncluir = ListUtils.subtract(
				listaServidorUnidadesFuncionais,
				listaServidorUnidadesFuncionaisFromDB);
		// chamada de inserção para cada uma
		for (MpmServidorUnidFuncional mpmServUnidadeFuncional : paraIncluir) {
			mpmServUnidadeFuncional.setCriadoEm(new Date());
			dao.persistir(mpmServUnidadeFuncional);
			dao.flush();
		}
		// lista das associações para excluir do banco
		List<MpmServidorUnidFuncional> paraExcluir = ListUtils.subtract(
				listaServidorUnidadesFuncionaisFromDB,
				listaServidorUnidadesFuncionais);
		for (MpmServidorUnidFuncional mpmServUnidadeFuncional : paraExcluir) {
			dao.remover(mpmServUnidadeFuncional);
			dao.flush();
		}
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			String paramString) {
		List<AghUnidadesFuncionais> result = getAghuFacade()
				.pesquisarPorDescricaoCodigoAtivaAssociada(paramString);
		if (result == null) {
			result = new ArrayList<AghUnidadesFuncionais>();
		}
		return result;
	}

	public List<AghAtendimentos> getListaAtendimentos(RapServidores servidor)
			throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmPacAtendProfissionalDAO dao = getMpmPacAtendProfissionaisDAO();
		List<AghAtendimentos> result = dao
				.pesquisarAtendimentosPorServidor(servidor);
		if (result == null) {
			result = new ArrayList<AghAtendimentos>();
		}
		return result;
	}

	public MpmListaPacCpa getPacienteCuidadosPosAnestesicos(
			RapServidores servidor) {
		MpmListaPacCpaDAO dao = getMpmListaPacCpasDAO();
		MpmListaPacCpa result = dao.busca(servidor);
		return result;
	}

	
	public void salvarIndicadorPacientesAtendimento(boolean indicadorPacCPA,
			RapServidores servidor) throws ApplicationBusinessException {
		MpmListaPacCpaDAO dao = getMpmListaPacCpasDAO();
		MpmListaPacCpa cpas = dao.busca(servidor);
		if (cpas == null) {
			cpas = new MpmListaPacCpa();
			cpas.setId(new MpmListaPacCpaId(servidor.getId().getMatricula(),
					servidor.getId().getVinCodigo()));
			cpas.setServidor(servidor);
			cpas.setIndPacCpa(indicadorPacCPA);
			cpas.setCriadoEm(new Date());
			dao.persistir(cpas);
			dao.flush();
		} else {
			cpas.setIndPacCpa(indicadorPacCPA);
			cpas.setAlteradoEm(new Date());
			dao.merge(cpas);
			dao.flush();
		}
	}

	public List<AghAtendimentos> getListaAtendimentos(Integer prontuario)
			throws ApplicationBusinessException {
		List<AghAtendimentos> result = getAghuFacade()
				.pesquisarPorProntuarioPacienteLista(prontuario);
		if (result == null) {
			result = new ArrayList<AghAtendimentos>();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	
	public void salvarListaAtendimentos(
			List<AghAtendimentos> listaAtendimentos, RapServidores servidor)
			throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmPacAtendProfissional associacao = null;
		MpmPacAtendProfissionalId idAssociacao = null;
		// Lista com os itens que vieram da tela
		List<MpmPacAtendProfissional> listaServidorAtendimentos = new ArrayList<MpmPacAtendProfissional>();
		for (AghAtendimentos atendimento : listaAtendimentos) {
			idAssociacao = new MpmPacAtendProfissionalId(atendimento.getSeq(),
					servidor.getId().getMatricula(), servidor.getId()
							.getVinCodigo());
			associacao = new MpmPacAtendProfissional(idAssociacao);
			associacao.setAtendimento(atendimento);
			associacao.setServidor(servidor);
			listaServidorAtendimentos.add(associacao);
		}
		// carrega os dados atuais do banco
		MpmPacAtendProfissionalDAO dao = getMpmPacAtendProfissionaisDAO();
		List<MpmPacAtendProfissional> listaServidorAtendimentosFromDB = dao
				.pesquisarAssociacoesPorServidor(servidor);
		// lista das associações para incluir no banco
		List<MpmPacAtendProfissional> paraIncluir = ListUtils.subtract(
				listaServidorAtendimentos, listaServidorAtendimentosFromDB);
		// chamada de inserção para cada uma
		for (MpmPacAtendProfissional mpmPacAtendProfissional : paraIncluir) {
			mpmPacAtendProfissional.setCriadoEm(new Date());
			dao.persistir(mpmPacAtendProfissional);
			dao.flush();
		}
		// lista das associações para excluir do banco
		List<MpmPacAtendProfissional> paraExcluir = ListUtils.subtract(
				listaServidorAtendimentosFromDB, listaServidorAtendimentos);
		for (MpmPacAtendProfissional mpmPacAtendProfissional : paraExcluir) {
			dao.remover(mpmPacAtendProfissional);
			dao.flush();
		}
	}

	public List<AghAtendimentos> pesquisaFoneticaAtendimentos(
			String nomePesquisaFonetica, String leitoPesquisaFonetica,
			String quartoPesquisaFonetica,
			AghUnidadesFuncionais unidadeFuncionalPesquisaFoneticaSelecionada) {		
		List<AghAtendimentos> result = getAghuFacade().pesquisaFoneticaAtendimentos(
				nomePesquisaFonetica, leitoPesquisaFonetica,
				quartoPesquisaFonetica,
				unidadeFuncionalPesquisaFoneticaSelecionada);
		if (result == null) {
			result = new ArrayList<AghAtendimentos>();
		}
		return result;
	}

	protected MpmListaPacCpaDAO getMpmListaPacCpasDAO() {
		return mpmListaPacCpaDAO;
	}

	protected MpmPacAtendProfissionalDAO getMpmPacAtendProfissionaisDAO() {
		return mpmPacAtendProfissionalDAO;
	}

	protected MpmServidorUnidFuncionalDAO getMpmServidorUnidFuncionaisDAO() {
		return mpmServidorUnidFuncionalDAO;
	}

	protected MpmListaServEspecialidadeDAO getMpmListaServEspecialidadesDAO() {
		return mpmListaServEspecialidadeDAO;
	}

	protected MpmListaServEquipeDAO getMpmListaServEquipesDAO() {
		return mpmListaServEquipeDAO;
	}

	protected MpmListaServResponsavelDAO getMpmListaServResponsavelDAO() {
		return mpmListaServResponsavelDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
		
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
