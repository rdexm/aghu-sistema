package br.gov.mec.aghu.exames.patologia.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class GuicheController extends ActionController {

	private static final Log LOG = LogFactory.getLog(GuicheController.class);

	private static final long serialVersionUID = 0L;

	private static final String GUICHE_LIST = "guicheList";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private AelCadGuiche guiche;

	private AghTiposUnidadeFuncional aghTiposUnidadeFuncional;

	private Short seqGuiche;

	private boolean consulta = false;

	private String voltarPara;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (seqGuiche != null) {
			guiche = examesPatologiaFacade.obterAelGuiche(seqGuiche);
			seqGuiche = null;
			
			if(guiche == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			guiche = new AelCadGuiche();
		}
		
		return null;
	
	}

	public String gravar() {
		try {
			if (guiche != null) {
				
				final boolean novo = guiche.getSeq() == null;
				if (novo) {
					this.examesPatologiaFacade.inserirAelCadGuiche(guiche);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_CAD_GUICHE_INSERT_SUCESSO", guiche.getDescricao());
					
				} else {
					String nomeMicrocomputador = null;
					try {
						nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
					} catch (UnknownHostException e) {
						throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR, e);
					}
					this.examesPatologiaFacade.alterarAelCadGuiche(guiche, nomeMicrocomputador);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_CAD_GUICHE_UPDATE_SUCESSO",guiche.getDescricao());
				}
				
				return cancelar();
			}
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	public String cancelar() {
		guiche = null;
		seqGuiche = null;
		return GUICHE_LIST;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final String parametro) {
		if (this.aghTiposUnidadeFuncional == null) {
			try {
				final AghParametros aghParametros = parametroFacade.obterAghParametro(AghuParametrosEnum.P_TIPO_UNIDADE_FUNCIONAL_LABORATORIO);
				aghTiposUnidadeFuncional = aghuFacade.obterTiposUnidadeFuncional(aghParametros.getVlrNumerico().intValue());
			} catch (final ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		return this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(parametro,
				aghTiposUnidadeFuncional, 100);
	}

	public void setConsulta(final boolean consulta) {
		this.consulta = consulta;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setVoltarPara(final String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setGuiche(final AelCadGuiche guiche) {
		this.guiche = guiche;
	}

	public AelCadGuiche getGuiche() {
		return guiche;
	}

	public void setSeqGuiche(final Short seqGuiche) {
		this.seqGuiche = seqGuiche;
	}

	public Short getSeqGuiche() {
		return seqGuiche;
	}

}
