package br.gov.mec.aghu.faturamento.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.FatVlrItemProcedHospCompsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterValoresProcedimentoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterValoresProcedimentoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8057130511046159706L;

    private static final String MANTER_ITEM_PRINCIPAL = "faturamento-manterItemPrincipal";

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private List<FatVlrItemProcedHospComps> listaVlrItemProced;

	private Integer seq;

	private Short phoSeq;

	private FatItensProcedHospitalar itemProcedHosp;

	private FatVlrItemProcedHospComps vigente;

	private FatVlrItemProcedHospComps entityEdited;

	private boolean novo;
	
	private boolean ativo = false;


	public enum ManterValoresProcedimentoExceptionCode implements BusinessExceptionCode {
		VALOR_ITEM_PROCEDIMENTO_PARAMENTROS_INVALIDOS, VALOR_ITEM_PROCEDIMENTO_DATA_INVALIDA, VALOR_ITEM_PROCEDIMENTO_DATA_INICIAL_INVALIDA, VALOR_ITEM_PROCEDIMENTO_DATA_INICIAL_ANTES_INICIAL_INVALIDA
	}

	public void inicio() throws ApplicationBusinessException {

        if (getPhoSeq() == null || getSeq() == null) {
            throw new ApplicationBusinessException(ManterValoresProcedimentoExceptionCode.VALOR_ITEM_PROCEDIMENTO_PARAMENTROS_INVALIDOS);
        }
        FatItensProcedHospitalarId id = new FatItensProcedHospitalarId();
        id.setPhoSeq(this.phoSeq);
        id.setSeq(this.seq);
        setItemProcedHosp(this.faturamentoFacade.obterItemProcedHospitalarPorChavePrimaria(id));
        inicializaEntity();
        setListaVlrItemProced(faturamentoFacade.obterListaValorItemProcHospComp(getPhoSeq(), getSeq()));
        if (getListaVlrItemProced() == null || getListaVlrItemProced().isEmpty()) {
            setVigente(null);
            setListaVlrItemProced(new ArrayList<FatVlrItemProcedHospComps>());
        } else {
            for (FatVlrItemProcedHospComps comps : getListaVlrItemProced()) {
                realizarTotalizacao(comps);
            }
            setVigente(getListaVlrItemProced().get(0));
        }
	
	}


	private void inicializaEntity() {
		setEntityEdited(new FatVlrItemProcedHospComps());
		getEntityEdited().setId(new FatVlrItemProcedHospCompsId());
		setNovo(true);
	}

	public Long recuperarCount() {
		return (long) Integer.MAX_VALUE;
	}

	@SuppressWarnings("rawtypes")
	public List recuperarListaPaginada(final Integer arg0, final Integer arg1, final String arg2, final boolean arg3) {
		return getListaVlrItemProced();
	}

	private void validarCampos(FatVlrItemProcedHospComps vlrItemProcedHospComps) throws ApplicationBusinessException {
		faturamentoFacade.validarCamposFatVlrItemProcedHospComps(vlrItemProcedHospComps, getVigente());
	}

	private void realizarTotalizacao(FatVlrItemProcedHospComps comps) {
		comps.setVlrTotalAmb(comps.getVlrProcedimento() == null ? BigDecimal.ZERO : comps.getVlrProcedimento());
		comps.setVlrTotalInt(somarTotal(comps.getVlrServHospitalar() == null ? BigDecimal.ZERO : comps.getVlrServHospitalar(),
				comps.getVlrServProfissional()));
	}

	private BigDecimal somarTotal(BigDecimal origem, final BigDecimal... adicionado) {
		if (adicionado != null && adicionado.length > 0) {
			for (final BigDecimal add : adicionado) {
				if (add != null) {
					origem = origem.add(add);
				}
			}
		}
		return origem;
	}

	public String gravar() throws ApplicationBusinessException, ApplicationBusinessException {
		// Salva todos os dados
		try {
			if (getListaVlrItemProced() == null || getListaVlrItemProced().isEmpty()) {
				apresentarMsgNegocio(Severity.WARN, "VALORES_PROCEDIMENTO_HOSPITALAR_SEM_DADOS");
			} else {
				if (getListaVlrItemProced().size() == 1) {
					faturamentoFacade.validarCamposFatVlrItemProcedHospComps(getVigente(), null);
					faturamentoFacade.persistirFatVlrItemProcedHospComps(getVigente(), getSeq(), getPhoSeq());
				} else {
                    FatVlrItemProcedHospComps vigenciaAnterior = encontraVigenciaAnterior();
                    validaCompetenciaEmRelacaoACompetenciaAnterior(vigenciaAnterior);
                    faturamentoFacade.persistirFatVlrItemProcedHospComps(vigenciaAnterior, getSeq(), getPhoSeq());
					int i = 1;
					while (getVigente().getId().getIphPhoSeq() == null || getVigente().getId().getIphSeq() == null) {
						if (getListaVlrItemProced().size() > 1) {
							faturamentoFacade.validarCamposFatVlrItemProcedHospComps(getVigente(), getListaVlrItemProced().get(i));
						}
						faturamentoFacade.persistirFatVlrItemProcedHospComps(getVigente(), getSeq(), getPhoSeq());
						if (getListaVlrItemProced().size() == (i + 1)) {
							FatVlrItemProcedHospComps ultimo = getListaVlrItemProced().get(i);
							// if(ultimo.getId().getIphPhoSeq() == null ||
							// ultimo.getId().getIphSeq() == null){
							faturamentoFacade.validarCamposFatVlrItemProcedHospComps(ultimo, null);
							faturamentoFacade.persistirFatVlrItemProcedHospComps(ultimo, getSeq(), getPhoSeq());
							i = Integer.MAX_VALUE;
							// }
							break;
						}
						setVigente(getListaVlrItemProced().get(i++));
					}
					if (getListaVlrItemProced().size() >= i) {
						faturamentoFacade.validarCamposFatVlrItemProcedHospComps(getVigente(), getListaVlrItemProced().get(i));
						faturamentoFacade.persistirFatVlrItemProcedHospComps(getVigente(), null, null);
					}
				}
				apresentarMsgNegocio(Severity.INFO, "VALORES_PROCEDIMENTO_HOSPITALAR_ALTERADA_SUCESSO");
			}
		} catch (BaseException e) {
			LOG.error("Exceção capturada: ",e);
			apresentarExcecaoNegocio(e);
			return null;
		}

		return cancelar();
	}

    private FatVlrItemProcedHospComps encontraVigenciaAnterior() {
        for (FatVlrItemProcedHospComps item : getListaVlrItemProced()) {
            if (item.getId().getIphPhoSeq() != null
                    && item.getId().getIphSeq() != null) {
                return item;
            }
        }

        return null;
    }

    private void validaCompetenciaEmRelacaoACompetenciaAnterior(FatVlrItemProcedHospComps competencia) throws ApplicationBusinessException{
        int indice = getListaVlrItemProced().indexOf(competencia);

        if (indice < (getListaVlrItemProced().size() -1)) {
            FatVlrItemProcedHospComps competenciaAnterior = getListaVlrItemProced().get(indice + 1);
            faturamentoFacade.validarCamposFatVlrItemProcedHospComps(competencia, competenciaAnterior);
        }
    }

	public void adicionar() {
		try {
			// getEntityEdited().getId().setDtInicioCompetencia(gerarDataHora(getEntityEdited().getId().getDtInicioCompetencia()));
			// if (getVigente().getDtFimCompetencia() == null) {
			// Date dtFim = new
			// Date(getEntityEdited().getId().getDtInicioCompetencia().getTime()
			// - 1000);
			// getVigente().setDtFimCompetencia(dtFim);
			// }

			validarCampos(getEntityEdited());
            //lembrar que a ultima competencia na verdade é o primeiro elemento da lista
            FatVlrItemProcedHospComps ultimoElemento = null;
            if (getListaVlrItemProced() != null && !getListaVlrItemProced().isEmpty()) {
                ultimoElemento = getListaVlrItemProced().get(0);
            }

			if (ultimoElemento != null && ultimoElemento.getDtFimCompetencia() == null) {
                ultimoElemento.setDtFimCompetencia(new Date(getEntityEdited().getId().getDtInicioCompetencia().getTime() - 1000));
			}

			realizarTotalizacao(getEntityEdited());
			getEntityEdited().setFatItensProcedHospitalar(getItemProcedHosp());
			listaVlrItemProced.add(0, getEntityEdited());
			setVigente(getEntityEdited());
			inicializaEntity();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editar() {
		if (getVigente().getId().getIphPhoSeq() == null) {
			setEntityEdited(new FatVlrItemProcedHospComps(getVigente()));
		} else {
			// setEntityEdited(faturamentoFacade.obterFatVlrItemProcedHospComps(getVigente().getId()));
			setEntityEdited(getVigente());
		}
		setNovo(false);
	}

	public void atualizar() {
		try {
			// if(getEntityEdited().getDtFimCompetencia()!= null){
			// getEntityEdited().setDtFimCompetencia(gerarDataHora(getEntityEdited().getDtFimCompetencia()));
			// }
			validarCampos(getEntityEdited());
			realizarTotalizacao(getEntityEdited());
			getListaVlrItemProced().remove(getVigente());
			getListaVlrItemProced().add(0, getEntityEdited());
			setVigente(getEntityEdited());
			inicializaEntity();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao() {
		inicializaEntity();
	}

	// private Date gerarDataHora(Date data) {
	// Calendar agora = Calendar.getInstance();
	// Calendar informado = Calendar.getInstance();
	// informado.setTime(data);
	// agora.set(Calendar.DATE, informado.get(Calendar.DATE));
	// agora.set(Calendar.MONTH, informado.get(Calendar.MONTH));
	// agora.set(Calendar.YEAR, informado.get(Calendar.YEAR));
	// return agora.getTime();
	// }

	public String cancelar() {
		return MANTER_ITEM_PRINCIPAL;
	}

	public void setListaVlrItemProced(final List<FatVlrItemProcedHospComps> listaVlrItemProced) {
		this.listaVlrItemProced = listaVlrItemProced;
	}

	public List<FatVlrItemProcedHospComps> getListaVlrItemProced() {
		return listaVlrItemProced;
	}

	public void setEntityEdited(FatVlrItemProcedHospComps entityEdited) {
		this.entityEdited = entityEdited;
	}

	public FatVlrItemProcedHospComps getEntityEdited() {
		return entityEdited;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		this.itemProcedHosp = itemProcedHosp;
	}

	public FatItensProcedHospitalar getItemProcedHosp() {
		return itemProcedHosp;
	}

	public void setVigente(FatVlrItemProcedHospComps vigente) {
		this.vigente = vigente;
	}

	public FatVlrItemProcedHospComps getVigente() {
		return vigente;
	}

	public void setNovo(boolean novo) {
		this.novo = novo;
	}

	public boolean isNovo() {
		return novo;
	}


	public boolean isAtivo() {
		return ativo;
	}


	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}