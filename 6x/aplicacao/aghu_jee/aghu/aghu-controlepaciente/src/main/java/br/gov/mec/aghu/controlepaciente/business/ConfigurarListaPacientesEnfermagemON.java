package br.gov.mec.aghu.controlepaciente.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpServidorUnidFuncionalDAO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EcpServidorUnidFuncional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConfigurarListaPacientesEnfermagemON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConfigurarListaPacientesEnfermagemON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EcpServidorUnidFuncionalDAO ecpServidorUnidFuncionalDAO;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1281767608019018192L;

	public enum ConfigurarListaPacientesEnfermagemONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SERVIDOR_INVALIDO;
	}


	public List<AghUnidadesFuncionais> buscarListaUnidadesFuncionais(RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					ConfigurarListaPacientesEnfermagemONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		EcpServidorUnidFuncionalDAO dao = getEcpServidorUnidFuncionalDAO();
		List<AghUnidadesFuncionais> result = dao.pesquisarUnidadesFuncionaisPorServidor(servidor);
		if (result == null) {
			result = new ArrayList<AghUnidadesFuncionais>();
		}
		return result;
	}
	

	public List<AghUnidadesFuncionais> pesquisarListaUnidadesFuncionais(String paramString) {
		
		List<AghUnidadesFuncionais> result = this.getAghuFacade()
				.pesquisarPorDescricaoCodigoAtivaAssociada(paramString);
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
					ConfigurarListaPacientesEnfermagemONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		EcpServidorUnidFuncional associacao = null;
		// Lista com os itens que vieram da tela
		List<EcpServidorUnidFuncional> listaServidorUnidadesFuncionais = new ArrayList<EcpServidorUnidFuncional>();
		for (AghUnidadesFuncionais unidadeFuncional : listaUnidadesFuncionais) {
			associacao = new EcpServidorUnidFuncional();
			associacao.setUnidadeFuncional(unidadeFuncional);
			associacao.setServidor(servidor);
			listaServidorUnidadesFuncionais.add(associacao);
		}
		// carrega os dados atuais do banco
		EcpServidorUnidFuncionalDAO dao = getEcpServidorUnidFuncionalDAO();
		List<EcpServidorUnidFuncional> listaServidorUnidadesFuncionaisFromDB = dao
				.pesquisarAssociacoesPorServidor(servidor);
		// lista das associações para incluir no banco
		List<EcpServidorUnidFuncional> paraIncluir = ListUtils.subtract(
				listaServidorUnidadesFuncionais,
				listaServidorUnidadesFuncionaisFromDB);
		// chamada de inserção para cada uma
		for (EcpServidorUnidFuncional ecpServUnidadeFuncional : paraIncluir) {
//			ecpServUnidadeFuncional.setCriadoEm(new Date());
			dao.persistir(ecpServUnidadeFuncional);
			dao.flush();
		}
		// lista das associações para excluir do banco
		List<EcpServidorUnidFuncional> paraExcluir = ListUtils.subtract(
				listaServidorUnidadesFuncionaisFromDB,
				listaServidorUnidadesFuncionais);
		for (EcpServidorUnidFuncional mpmServUnidadeFuncional : paraExcluir) {
			dao.remover(mpmServUnidadeFuncional);
			dao.flush();
		}
	}

	protected EcpServidorUnidFuncionalDAO getEcpServidorUnidFuncionalDAO() {
		return ecpServidorUnidFuncionalDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
