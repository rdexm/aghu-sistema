package br.gov.mec.aghu.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * ORADB: <code>AGH_CONFIG</code>
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@SuppressWarnings("PMD.AtributoEmSeamContextManager")
@Stateless
public class AghuConfigRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AghuConfigRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private AghParametrosDAO aghParametrosDAO; 

	/**
	 * 
	 */
	private static final long serialVersionUID = -1617534462919675182L;

	private String razaoSocial = "Hospital de Clínicas de Porto Alegre";
	private String endereco = "Rua Ramiro Barcelos, 2350";
	private String cidade = "Porto Alegre";
	private String cgc = "87.020.517/0001-20";
	private String fax = "(051) 316-8030"; // "FAX_GRUM"
	private String nomeAplicacao = "Aplicativos para Gestão Hospitalar";
	private Integer ccustAtuacao;
	private Integer uasg;
	
	protected IParametroFacade getParametroFacade() {		
		return parametroFacade;
	}
	
	protected AghParametros obterAghParametro(AghuParametrosEnum param) throws ApplicationBusinessException {
		return getParametroFacade().buscarAghParametro(param);		
	}

	/**
	 * ORADB: <code>AGH_CONFIG.RAZAO_SOCIAL</code>
	 * TODO
	 * @return
	 */
	public String getRazaoSocial() {
		
		AghParametros param = null;
		
		try {
			param = this.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			this.razaoSocial = param.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			this.logError(e);
		}
		
		return this.razaoSocial;
	}

	/**
	 * ORADB: <code>AGH_CONFIG.ENDERECO</code>
	 * TODO
	 * @return
	 */
	public String getEndereco() {
		
		AghParametros param = null;

		try {
			param = this.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO);
			this.endereco = param.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			this.logError(e);
		}

		return this.endereco;
	}

	/**
	 * ORADB: <code>AGH_CONFIG.CIDADE</code>
	 * TODO
	 * @return
	 */
	public String getCidade() {
		
		AghParametros param = null;

		try {
			param = this.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
			this.cidade = param.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			this.logError(e);
		}
		return this.cidade;
	}

	/**
	 * ORADB: <code>AGH_CONFIG.CGC</code>
	 * TODO
	 * @return
	 */
	public String getCgc() {
		
		AghParametros param = null;

		try {
			param = this.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_CGC);
			this.cgc = param.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			this.logError(e);
		}

		return this.cgc;
	}

	/**
	 * ORADB: <code>AGH_CONFIG.FAX</code>
	 * TODO
	 * @return
	 */
	public String getFax() {		
		
		AghParametros param = null;

		try {
			param = this.obterAghParametro(AghuParametrosEnum.FAX_GRUM);
			this.cgc = param.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			this.logError(e);
		}

		return this.fax;
	}

	/**
	 * ORADB: <code>AGH_CONFIG.NOME_APLICACAO</code>
	 * TODO
	 * @return
	 */
	public String getNomeAplicacao() {
		
		AghParametros param = null;

		try {
			param = this.obterAghParametro(AghuParametrosEnum.P_NOME_APLICACAO);
			this.nomeAplicacao = param.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			this.logError(e);
		}

		return this.nomeAplicacao;
	}

	/**
	 * REDMINE: <code>9675</code>
	 * @return
	 */
	public Integer getUasg() {

		AghParametros param = null;
		BigDecimal val = null;

		try {
			param = this.obterAghParametro(AghuParametrosEnum.P_UNIDADE_ADMINISTRATIVA_DE_SERVICOS_GERAIS);
			val = param.getVlrNumerico();
			if (val != null) {
				this.uasg = Integer.valueOf(val.intValue());				
			}
		} catch (ApplicationBusinessException e) {
			this.logError(e);
		}

		return this.uasg;
	}
	
	/**
	 * ORADB: <code>AGH_CONFIG.CCUST_ATUACAO</code> 
	 * @return
	 */
	public Integer getCcustAtuacao(RapServidores servidor) {
		try {
			if (this.ccustAtuacao == null) {
				if (servidor.getCentroCustoAtuacao() != null) {
					this.ccustAtuacao = servidor.getCentroCustoAtuacao()
							.getCodigo();
				} else {
					this.ccustAtuacao = servidor.getCentroCustoLotacao()
							.getCodigo();
				}
			}
		} catch (Exception e) {
			logError("ExceÃ§Ã£o capturada: ", e);
			this.ccustAtuacao = null;
		}
		return this.ccustAtuacao;
	}

	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	public Boolean isHCPA() {
		return super.isHCPA();
	}
	
	public Date buscaDataHoraBancoDeDados() {
		return aghParametrosDAO.obterDataHoraBanco();
	}



}