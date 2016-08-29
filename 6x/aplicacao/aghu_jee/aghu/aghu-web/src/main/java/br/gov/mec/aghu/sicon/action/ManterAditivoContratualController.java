package br.gov.mec.aghu.sicon.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAditContratoId;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;

public class ManterAditivoContratualController extends ActionController {

	private static final long serialVersionUID = 1308195042046060091L;
	
	private static final String PAGE_GERENCIAR_CONTRATOS = "gerenciarContratos";

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;


	private String periodo;

	/** The sco contrato. */
	private ScoContrato scoContrato;

	private ScoAditContrato aditContrato;

	private String contSeq;

	private Integer aditSeq;

	private List<ScoAditContrato> items;

	/** The is edit. */
	private Boolean isEdit = false;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void init() {
	 

		try {

			this.scoContrato = siconFacade.getContrato(Integer.parseInt(getContSeq()));
			items = siconFacade.listarAditivosByContrato(scoContrato);
			if (!isEdit) {
				cleanForm();
			} else {
				calculaDataDiferenca();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	public void gravar() {
		try {

			boolean isUpdate = this.aditContrato.getId().getSeq() != null;
			this.aditContrato.setObservacao(StringUtils.strip(this.aditContrato.getObservacao()));
			this.aditContrato.setJustificativa(StringUtils.strip(this.aditContrato.getJustificativa()));
			aditContrato.setJustificativa(StringUtil.trim(aditContrato.getJustificativa()));
			if (aditContrato.getObservacao() != null) {
				aditContrato.setObservacao(StringUtil.trim(aditContrato.getObservacao().trim()));
				aditContrato.setObservacao(trataLetraInicial(aditContrato.getObservacao()));
			}
			if (aditContrato.getObjetoContrato() != null) {
				aditContrato.setObjetoContrato(StringUtil.trim(aditContrato.getObjetoContrato().trim()));
				aditContrato.setObjetoContrato(trataLetraInicial(aditContrato.getObjetoContrato()));
			}
			aditContrato = siconFacade.gravarOrAtualizarAditivo(aditContrato);
			this.isEdit = false;
			if (!isUpdate) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ADIT_CONTRATO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ADIT_CONTRATO");
			}

			items = siconFacade.listarAditivosByContrato(scoContrato);
			cleanForm();
			limpar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String editar(ScoAditContrato input) {
		
		if (input.getTipoContratoSicon() != null) {
		   input.setTipoContratoSicon(cadastrosBasicosSiconFacade.obterTipoContratoSicon(input.getTipoContratoSicon().getSeq()));
		}
		
		this.setIsEdit(true);
		this.setAditContrato(input);
		
		calculaDataDiferenca();
		return null;
	}

	public void excluir() {
		try {
			siconFacade.excluirAditContrato(new ScoAditContratoId(aditSeq, scoContrato.getSeq()));
			items = siconFacade.listarAditivosByContrato(scoContrato);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ADIT_CONTRATO");

	}

	public String voltar() {
		limpar();
		return PAGE_GERENCIAR_CONTRATOS;
	}

	public void limpar() {
		cleanForm();
		this.isEdit = false;
	}

	/**
	 * Transforma em uppercase a primeira letra da string de entrada.
	 * 
	 * @param _textoOriginal
	 * @return string contendo a entrada com a inicial uppercase.
	 */
	public String trataLetraInicial(String _textoOriginal) {

		StringBuffer texto = new StringBuffer(_textoOriginal);

		texto.setCharAt(0, Character.toUpperCase(_textoOriginal.charAt(0)));

		return texto.toString();

	}

	public List<ScoTipoContratoSicon> listarTiposContratoAtivos(String pesquisa) {
		List<ScoTipoContratoSicon> tiposContrato = cadastrosBasicosSiconFacade.listarTiposContratoComAditivo(pesquisa);
		return tiposContrato;
	}

	public String getCpfCnpj() {

		if (this.scoContrato != null && this.scoContrato.getFornecedor() != null
				&& this.scoContrato.getFornecedor().getCgc() != null) {
			return CoreUtil.formatarCNPJ(this.scoContrato.getFornecedor().getCgc());
		} else if (this.scoContrato != null && this.scoContrato.getFornecedor() != null
				&& this.scoContrato.getFornecedor().getCpf() != null) {
			return CoreUtil.formataCPF(this.scoContrato.getFornecedor().getCpf());
		}

		return "";
	}

	private void cleanForm() {
		aditContrato = new ScoAditContrato();
		aditContrato.setSituacao(DominioSituacaoEnvioContrato.A);
		aditContrato.setId(new ScoAditContratoId(null, scoContrato.getSeq()));
		aditContrato.setCont(scoContrato);
		setPeriodo(null);
	}

	public void calculaDataDiferenca() {
		setPeriodo(calcDiff(aditContrato.getDtInicioVigencia(), aditContrato.getDtFimVigencia()));
	}

	public String calcDiff(Date d1, Date d2) {
		if (d1 != null ^ d2 != null) {
			return "Periodo invalido";
		} else if (d1 == null && d2 == null) {
			return "";
		} else {
			if (d1.compareTo(d2) > 0) {
				return "Periodo invalido";
			}
			Period p = new Period(d1.getTime(), d2.getTime(), PeriodType.yearMonthDay());
			StringBuffer s = new StringBuffer();
			if (p.getYears() > 0) {
				s.append(p.getYears());
				if (p.getYears() == 1) {
					s.append(" ano ");
				} else {
					s.append(" anos ");
				}
			}
			if (p.getMonths() > 0) {
				s.append(p.getMonths());
				if (p.getMonths() == 1) {
					s.append(" mês ");
				} else {
					s.append(" meses ");
				}
			}
			if (p.getDays() > 0) {
				s.append(p.getDays());
				if (p.getDays() == 1) {
					s.append(" dia ");
				} else {
					s.append(" dias ");
				}
			}
			return s.toString();
		}
	}

	public Boolean habilitarExclusao(ScoAditContrato aditivo) {

		return siconFacade.indicarExclusaoAditivo(aditivo);
	}

	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}

	public ScoAditContrato getAditContrato() {
		return aditContrato;
	}

	public void setAditContrato(ScoAditContrato aditContrato) {
		this.aditContrato = aditContrato;
	}

	public List<ScoAditContrato> getItems() {
		return items;
	}

	public void setItems(List<ScoAditContrato> items) {
		this.items = items;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public String getContSeq() {
		return contSeq;
	}

	public void setContSeq(String contSeq) {
		this.contSeq = contSeq;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public Integer getAditSeq() {
		return aditSeq;
	}

	public void setAditSeq(Integer aditSeq) {
		this.aditSeq = aditSeq;
	}

}
