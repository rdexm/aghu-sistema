package br.gov.mec.aghu.sicon.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoConvItensContrato;
import br.gov.mec.aghu.model.ScoConvItensContratoId;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterConvenioItemContratoController extends ActionController {

	private static final long serialVersionUID = -7540221478159662727L;

	private static final String PAGE_MANTER_ITENS_CONTRATO = "manterItensContrato";

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private IOrcamentoFacade orcamentoFacade;

	private Integer seqContrato;
	private Long nrContrato;
	private Integer iconSeq; // Identificador do item
	private Integer cvfCodigo; // Identificador do convênio
	private ScoContrato contrato;
	private ScoItensContrato itemContrato;
	
	private BigDecimal valorTotal;
	private BigDecimal valorRateio;
	private Boolean incluir = false;
	private boolean existeConvenio;
	private Boolean isEdit = false;
	private ScoConvItensContrato convItemContrato = null;

	private FsoConveniosFinanceiro convenio;
	private List<ScoConvItensContrato> listaConveniosItens;
	private List<ScoConvItensContratoId> listaIdConveniosItensExclusao = new ArrayList<ScoConvItensContratoId>();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Método de inicialização da tela
	 */
	public void iniciar() {
		try {
			if (this.iconSeq != null) {
				this.itemContrato = this.siconFacade.getItemContratoBySeq(this.iconSeq);

				this.setValorTotal(this.itemContrato.getVlUnitario().multiply(new BigDecimal(this.itemContrato.getQuantidade())));
			}

			if (this.itemContrato != null) {
				setSeqContrato(this.itemContrato.getContrato().getSeq());
				setNrContrato(this.itemContrato.getContrato().getNrContrato());
				this.contrato = this.siconFacade.getContrato(seqContrato);
			}

			listaConveniosItens = this.siconFacade.listarConvItensContratos(this.itemContrato);
			this.setConvenio(null);
			this.setValorRateio(null);		 			 
			setIsEdit(false);

			setExisteConvenio(listaConveniosItens != null && !listaConveniosItens.isEmpty());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Listagem de convênios financeiros ativos
	 * 
	 * @param _input
	 *            Critério de filtragem para busca e preenchimento da listagem
	 * @return Lista de {@code FsoConveniosFinanceiro}, ativos, filtrada pelo
	 *         parâmetro de entrada
	 */
	public List<FsoConveniosFinanceiro> listarConveniosFinanceirosAtivos(String _input) {

		List<FsoConveniosFinanceiro> listaConvenios = null;

		try {
			listaConvenios = this.orcamentoFacade.listarFsoConveniosAtiva(_input);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return listaConvenios;
	}

	/**
	 * Adiciona um convênio a lista de convênios de itens.
	 * 
	 * @param _convenio
	 *            Convênio a ser adicionado.
	 * @throws BaseException
	 */
	public String adicionarConvenioAoItem() throws BaseException {
		if (valorRateio.equals(BigDecimal.ZERO)) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_OBRIGATORIO");
			return null;
		}

		if (convenio == null) {
			return null;
		}

		if (isEdit == true) {
			for (ScoConvItensContrato item : listaConveniosItens) {
				if (item.getId().getIconSeq().equals(iconSeq) && item.getId().getCvfCodigo().equals(cvfCodigo)) {
					convItemContrato = item;
				}
			}

			getConvItemContrato().setFsoConveniosFinanceiro(this.convenio);
			getConvItemContrato().setValorRateio(this.valorRateio);
			getConvItemContrato().setEditando(false);
			setCvfCodigo(null);
			setIconSeq(null);
			setIsEdit(false);
		} else {
			for (ScoConvItensContrato item : listaConveniosItens) {
				if (item.getFsoConveniosFinanceiro().getCodigo() == convenio.getCodigo()) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JA_INFORMADO");
					return null;

				}
			}

			convItemContrato = new ScoConvItensContrato();
			convItemContrato.setId(new ScoConvItensContratoId(itemContrato.getSeq(), convenio.getCodigo()));
			for (ScoConvItensContrato item : listaConveniosItens){
				item.setEditando(false);
			}convItemContrato.setFsoConveniosFinanceiro(this.convenio);
			convItemContrato.setScoItensContrato(itemContrato);
			convItemContrato.setValorRateio(this.valorRateio);

			this.listaConveniosItens.add(convItemContrato);
			setExisteConvenio(true);
		}

		this.setConvenio(null);
		this.setValorRateio(null);

		return null;

	}

	/**
	 * Altera registro selecionado no grid de convênio dos itens.
	 * 
	 */
	public void alterar(ScoConvItensContrato convItemContrato) {
		this.setConvenio(convItemContrato.getFsoConveniosFinanceiro());
		this.setValorRateio(convItemContrato.getValorRateio());
		setConvItemContrato(convItemContrato);
		setIsEdit(true);
		
		for (ScoConvItensContrato item : listaConveniosItens){
			item.setEditando(false);
		}
		convItemContrato.setEditando(true);		
	}

	/**
	 * Exclui registro da grid de convênios de itens de contrato.
	 */
	public void excluir(ScoConvItensContrato convItemContrato) {
		this.listaIdConveniosItensExclusao.add(new ScoConvItensContratoId(convItemContrato.getId().getIconSeq(), convItemContrato.getId().getCvfCodigo()));
		this.listaConveniosItens.remove(convItemContrato);
		this.setConvenio(null);
		this.setValorRateio(null);
		for (ScoConvItensContrato item : listaConveniosItens){
			item.setEditando(false);
		}		
	}

	/**
	 * Persiste no banco os dados selecionados na tela.
	 * 
	 */
	public String gravar() {
		try {
			this.siconFacade.gravarConvenioItemContrato(this.listaConveniosItens, listaIdConveniosItensExclusao, this.valorTotal);
			limpar();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CONVENIO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return PAGE_MANTER_ITENS_CONTRATO;

	}

	public void limpar() {
		//listaConveniosItens = null;
		try {
			listaConveniosItens = this.siconFacade.listarConvItensContratos(this.itemContrato);
			listaIdConveniosItensExclusao = new ArrayList<ScoConvItensContratoId>();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		this.setConvenio(null);
		this.setValorRateio(null);
		setIsEdit(false);
		
	}

	/**
	 * Voltar.
	 * 
	 * @return the string
	 */
	public String voltar() {
		return PAGE_MANTER_ITENS_CONTRATO;
	}

	/**
	 * Gets the cpf cnpj.
	 * 
	 * @return the cpf cnpj
	 */
	public String getCpfCnpj() {
		if (this.contrato != null && this.contrato.getFornecedor() != null && this.contrato.getFornecedor().getCgc() != null) {
			return CoreUtil.formatarCNPJ(this.contrato.getFornecedor().getCgc());
		} else if (this.contrato != null && this.contrato.getFornecedor() != null
				&& this.contrato.getFornecedor().getCpf() != null) {
			return CoreUtil.formataCPF(this.contrato.getFornecedor().getCpf());
		}
		return "";
	}

	// getters e setters

	public Integer getSeqContrato() {
		return seqContrato;
	}

	public void setSeqContrato(Integer seqContrato) {
		this.seqContrato = seqContrato;
	}

	public Integer getIconSeq() {
		return iconSeq;
	}

	public void setIconSeq(Integer iconSeq) {
		this.iconSeq = iconSeq;
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public ScoItensContrato getItemContrato() {
		return itemContrato;
	}

	public void setItemContrato(ScoItensContrato itemContrato) {
		this.itemContrato = itemContrato;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public List<ScoConvItensContrato> getListaConveniosItens() {
		return listaConveniosItens;
	}

	public void setListaConveniosItens(List<ScoConvItensContrato> listaConveniosItens) {
		this.listaConveniosItens = listaConveniosItens;
	}

	public BigDecimal getValorRateio() {
		return valorRateio;
	}

	public void setValorRateio(BigDecimal valorRateio) {
		this.valorRateio = valorRateio;
	}

	public Boolean getIncluir() {
		return incluir;
	}

	public void setIncluir(Boolean incluir) {
		this.incluir = incluir;
	}

	public List<ScoConvItensContratoId> getListaConveniosItensExclusao() {
		return listaIdConveniosItensExclusao;
	}

	public void setListaConveniosItensExclusao(List<ScoConvItensContratoId> listaIdConveniosItensExclusao) {
		this.listaIdConveniosItensExclusao = listaIdConveniosItensExclusao;
	}

	public FsoConveniosFinanceiro getConvenio() {
		return convenio;
	}

	public void setConvenio(FsoConveniosFinanceiro convenio) {
		this.convenio = convenio;
	}

	public Integer getCvfCodigo() {
		return cvfCodigo;
	}

	public void setCvfCodigo(Integer cvfCodigo) {
		this.cvfCodigo = cvfCodigo;
	}

	public Long getNrContrato() {
		return nrContrato;
	}

	public void setNrContrato(Long nrContrato) {
		this.nrContrato = nrContrato;
	}

	public boolean isExisteConvenio() {
		return existeConvenio;
	}

	public void setExisteConvenio(boolean existeConvenio) {
		this.existeConvenio = existeConvenio;
	}
	
	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public ScoConvItensContrato getConvItemContrato() {
		return convItemContrato;
	}

	public void setConvItemContrato(ScoConvItensContrato convItemContrato) {
		this.convItemContrato = convItemContrato;
	}	

}
