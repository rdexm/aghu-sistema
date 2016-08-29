package br.gov.mec.aghu.exames.business;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.exames.dao.AelAutorizacaoAlteracaoSituacaoDAO;
import br.gov.mec.aghu.model.AelAutorizacaoAlteracaoSituacao;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Regras de negocio para AelAutorizacaoAlteracaoSituacao.<br>
 * 
 * Tabela: ael_autoriz_alter_situacoes.<br>
 * 
 * @author rcorvalao
 *
 */
@Stateless
public class AelAutorizacaoAlteracaoSituacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelAutorizacaoAlteracaoSituacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICascaFacade cascaFacade;

@Inject
private AelAutorizacaoAlteracaoSituacaoDAO aelAutorizacaoAlteracaoSituacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6325751740938123567L;


	/**
	 * Verifica em <b>AelAutorizacaoAlteracaoSituacao</b> se o <b>servidor</b> 
	 * tem permissao para alterar exama na situacao <b>matrizSituacao</b>.<br>
	 * <p>
	 * Codigo baseado nos seguintes cursores do Oracle:<i>
	 * <li>
	 * cursor  c_permissao (c_mas_seq	ael_matriz_situacoes.seq-type) is
	 *       select  aas.per_nome
	 *       from  cse_perfis_usuarios pfu,
	 *             ael_autoriz_alter_situacoes aas
	 *       where  aas.mas_seq   =  c_mas_seq
	 *       and  pfu.per_nome  =  aas.per_nome
	 *       and  pfu.usr_id         =  user;</li>
	 * <li>
	 * cursor  c_permissao_serv (c_mas_seq	  ael_matriz_situacoes.seq-type
	 *                         , c_matricula  rap_servidores.ser_matricula-type
	 *                         , c_vin_codigo rap_servidores.ser_vin_codigo-type) is
	 *       select  aas.per_nome
	 *       from  cse_usuarios usr,
	 *             cse_perfis_usuarios pfu,
	 *             ael_autoriz_alter_situacoes aas
	 *       where  aas.mas_seq   =  c_mas_seq
	 *       and  pfu.per_nome  =  aas.per_nome
	 *       and  usr.id        =  pfu.usr_id
	 *       and  usr.ser_vin_codigo+0 = c_vin_codigo
	 *       and  usr.ser_matricula+0  = c_matricula;</li>
	 * </i></p>
	 * @param matrizSituacao
	 * @param servidor
	 */
	public boolean temPermissaoAlterarExameSituacao(AelMatrizSituacao matrizSituacao, RapServidores servidor) {
		boolean valueReturn = false;
		
		Set<String> nomePerfis = this.getICascaFacade().obterNomePerfisPorUsuario(servidor.getUsuario());
		
		List<AelAutorizacaoAlteracaoSituacao> autorizAltSituacoes = this.getAelAutorizacaoAlteracaoSituacaoDAO().listarPorMatriz(matrizSituacao);
		for (AelAutorizacaoAlteracaoSituacao autorizacao : autorizAltSituacoes) {
			if (nomePerfis.contains(autorizacao.getId().getPerNome())) {
				valueReturn = true;
				break;
			}
		}
		
		return valueReturn;
	}
	
	protected AelAutorizacaoAlteracaoSituacaoDAO getAelAutorizacaoAlteracaoSituacaoDAO() {
		return aelAutorizacaoAlteracaoSituacaoDAO;
	}
	
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}
	
}
