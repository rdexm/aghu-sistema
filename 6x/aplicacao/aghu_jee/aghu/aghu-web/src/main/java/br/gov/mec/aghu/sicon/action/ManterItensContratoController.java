package br.gov.mec.aghu.sicon.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoCadastroItemContrato;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * The Class ManterItensContratoController.
 */

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterItensContratoController extends ActionController {

	private static final long serialVersionUID = -1005805359088717899L;
	
	private static final String PAGE_MANTER_CONTRATO_MANUAL = "manterContratoManual";
	private static final String PAGE_MANTER_CONVENIO_ITEM_CONTRATO = "manterConvenioItemContrato";

	private enum ManterItensContratoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VALOR_UNIT_ZERO, MENSAGEM_QUANTIDADE_ZERO, MENSAGEM_VALOR_TOTAL_ZERO;
	}

	/** The sicon facade. */
	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private IOrcamentoFacade orcamentoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private String contSeq;

	private boolean setFieldsToReadonly;

	/** The sco contrato. */
	private ScoContrato scoContrato;

	/** The material comparator. */
	private static MaterialComparator materialComparator = new MaterialComparator();

	/** The is edit. */
	private Boolean isEdit = false;

	/** The material. */
	private ScoMaterial materialSelecionado, material;

	/** The servico. */
	private ScoServico servicoSelecionado, servico;
	private Integer itemSeq;
	private FsoConveniosFinanceiro convenio;

	private ScoMarcaComercial marcaSelecionada, marca;

	private DominioTipoCadastroItemContrato tipoCadastro;

	/** The item contrato. */
	private ScoItensContrato itemContrato;

	private ScoItensContrato popupItem;
	/** The itens. */
	private List<ScoItensContrato> itens;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Inits the flow.
	 */
	public void init() {
		try {
			if (this.tipoCadastro == null || StringUtils.isEmpty(this.tipoCadastro.getDescricao())) {
				this.tipoCadastro = DominioTipoCadastroItemContrato.S;
			}
			this.scoContrato = siconFacade.getContrato(Integer.parseInt(getContSeq()));

			if (scoContrato.getSituacao() == DominioSituacaoEnvioContrato.E
					&& siconFacade.listarAditivosByContrato(scoContrato).size() > 0) {
				this.setFieldsToReadonly = true;
			} else {
				this.setFieldsToReadonly = false;
			}

			itens = siconFacade.listarItensContratos(scoContrato);

			if (!isEdit) {
				itemContrato = new ScoItensContrato();
				this.itemContrato.setNrItem(findNextNrItem());
			} else {
				itemContrato.setVlTotal(itemContrato.getVlUnitario().multiply(new BigDecimal(itemContrato.getQuantidade())));
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	public DominioTipoCadastroItemContrato[] getSrvOnly() {
		DominioTipoCadastroItemContrato[] arrr = new DominioTipoCadastroItemContrato[1];
		arrr[0] = DominioTipoCadastroItemContrato.S;
		return arrr;
	}
	
	public String redirectManterConvenioItemContrato(){
		return PAGE_MANTER_CONVENIO_ITEM_CONTRATO;
	}

	/**
	 * Voltar.
	 * 
	 * @return the string
	 */
	public String voltar() {
		limpar(true);
		return PAGE_MANTER_CONTRATO_MANUAL;
	}

	/**
	 * Excluir.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public void excluir(ScoItensContrato input) {
		try {
			siconFacade.excluirItemContrato(input.getSeq());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirConfirma() {
		try {

			siconFacade.excluirItemContrato(itemSeq);
			setItens(siconFacade.listarItensContratos(scoContrato));
			limpar(true);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ITEM_CONTRATO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Editar.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public void editar(ScoItensContrato input) {
		this.setIsEdit(true);
		setItemContrato(input);
		doMultiply();
		if (input.getMaterial() != null) {
			setMaterial(input.getMaterial());
			setMarcaSelecionada(input.getMarcaComercial());
			setMarca(input.getMarcaComercial());
			setMaterialSelecionado(input.getMaterial());
			setTipoCadastro(DominioTipoCadastroItemContrato.M);
		} else {
			setServico(input.getServico());
			setServicoSelecionado(input.getServico());
			setTipoCadastro(DominioTipoCadastroItemContrato.S);
		}
	}

	/**
	 * Gravar.
	 * 
	 * @return the string
	 */
	public void gravar() {
		try {

			boolean isUpdate = this.itemContrato.getSeq() == null ? false : true;

			this.itemContrato.setNrItem(findNextNrItem());

			if (this.itemContrato != null) {
				if (this.itemContrato.getVlUnitario() == BigDecimal.ZERO || this.itemContrato.getVlUnitario() == null) {
					throw new ApplicationBusinessException(ManterItensContratoControllerExceptionCode.MENSAGEM_VALOR_UNIT_ZERO);
				} else if (this.itemContrato.getVlTotal() == BigDecimal.ZERO || this.itemContrato.getVlTotal() == null) {
					throw new ApplicationBusinessException(ManterItensContratoControllerExceptionCode.MENSAGEM_VALOR_TOTAL_ZERO);
				} else if (this.itemContrato.getQuantidade() == 0) {
					throw new ApplicationBusinessException(ManterItensContratoControllerExceptionCode.MENSAGEM_QUANTIDADE_ZERO);
				}
				if (this.tipoCadastro.getCodigo() == DominioTipoCadastroItemContrato.M.getCodigo()) {
					this.itemContrato.setMaterial(materialSelecionado);
					this.itemContrato.setMarcaComercial(marcaSelecionada);
					this.itemContrato.setDescricao(materialSelecionado.getNome());
				} else {
					this.itemContrato.setServico(servicoSelecionado);
					this.itemContrato.setDescricao(servicoSelecionado.getNome());
				}
				this.itemContrato.setContrato(this.scoContrato);
				siconFacade.gravarOrAtualizarItemContrato(itemContrato, getConvenio());
				if (!isUpdate) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ITEM_CONTRATO");
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZACAO_ITEM_CONTRATO");
				}
				this.setIsEdit(false);
				limpar(false);
				// this.itemContrato.setNrItem(findNextNrItem());

				itens = null;
				itens = siconFacade.listarItensContratos(scoContrato);				
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String getGridColumnConvenioTxt(ScoItensContrato input) {
		if (input.getConvItensContrato() != null) {
			setPopupItem(input);
			if (input.getConvItensContrato().size() == 0) {
				return "--";
			} else if (input.getConvItensContrato().size() == 1) {
				return input.getConvItensContrato().get(0).getFsoConveniosFinanceiro().getDescricao();
			} else {
				return String.valueOf(input.getConvItensContrato().size());
			}
		}
		return "--";
	}

	public void changeUnidMed() {
		this.itemContrato.setUnidade(null);
	}

	/**
	 * Selecionou material.
	 */
	public void selecionouMaterial() {
		setMaterialSelecionado(material);
		setUnidMedida();
	}
	
	public void limpar(boolean limparConvenio) {
		setMaterialSelecionado(null);
		setMarcaSelecionada(null);
		setServicoSelecionado(null);
		setMarca(null);
		setMaterial(null);
		setServico(null);
		//setTipoCadastro(DominioTipoCadastroItemContrato.S);
		setItemContrato(new ScoItensContrato());
		
		/**
		 * se um item for adicionado ao contrato, não limpa o conteúdo do
		 * convênio para que possa ser utilizado na próxima adição de item. se o
		 * usuário quiser um novo convênio, basta limpar o campo no próprio
		 * componente.
		 */
		if (limparConvenio) {
			setConvenio(null);
		}
		setIsEdit(false);

		//siconFacade.desatacharItemContrato(itemContrato);
		//setItemContrato(null);
	}

	/**
	 * Pesquisar material.
	 * 
	 * @param parametro
	 *            the parametro
	 * @return the list
	 */
	public List<ScoMaterial> pesquisarMaterial(String parametro) {
		String paramString = (String) parametro;
		Set<ScoMaterial> result = new HashSet<ScoMaterial>();
		if ((materialSelecionado == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String.valueOf(materialSelecionado.getCodigo())) || StringUtils
						.equalsIgnoreCase(paramString, String.valueOf(materialSelecionado.getNome())))) {
			try {
				result = new HashSet<ScoMaterial>(comprasFacade.getListaMaterialByNomeOrCodigo(paramString));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(materialSelecionado);
		}
		List<ScoMaterial> resultReturn = new ArrayList<ScoMaterial>(result);
		Collections.sort(resultReturn, materialComparator);
		return resultReturn;
	}

	/**
	 * Selecionou srv.
	 */
	public void selecionouSrv() {
		setServicoSelecionado(servico);
	}

	/**
	 * Pesquisar srv.
	 * 
	 * @param parametro
	 *            the parametro
	 * @return the list
	 */
	public List<ScoServico> pesquisarSrv(String parametro) {
		String paramString = (String) parametro;
		Set<ScoServico> result = new HashSet<ScoServico>();
		if ((servicoSelecionado == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String.valueOf(servicoSelecionado.getCodigo())) || StringUtils
						.equalsIgnoreCase(paramString, String.valueOf(servicoSelecionado.getNome())))) {

			result = new HashSet<ScoServico>(comprasFacade.listarServicosAtivos(paramString));
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(servicoSelecionado);
		}
		List<ScoServico> resultReturn = new ArrayList<ScoServico>(result);
		// Collections.sort(resultReturn, materialComparator);
		return resultReturn;
	}

	public void selecionouMarca() {
		setMarcaSelecionada(marca);
	}

	public String renderizarTela() {
		limpar(false);
		return null;
	}

	public List<ScoMarcaComercial> pesquisarMarcas(String parametro) {
		String paramString = (String) parametro;
		Set<ScoMarcaComercial> result = new HashSet<ScoMarcaComercial>();
		if ((marcaSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String.valueOf(marcaSelecionada.getCodigo())) || StringUtils
						.equalsIgnoreCase(paramString, String.valueOf(marcaSelecionada.getDescricao())))) {
			try {
				result = new HashSet<ScoMarcaComercial>(comprasFacade.getListaMarcasByNomeOrCodigo(paramString));
				// result.removeAll(getListaServEquipes());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(marcaSelecionada);
		}
		List<ScoMarcaComercial> resultReturn = new ArrayList<ScoMarcaComercial>(result);
		// Collections.sort(resultReturn, materialComparator);
		return resultReturn;
	}

	private void setUnidMedida() {
		this.itemContrato.setUnidade(getMaterialSelecionado().getUnidadeMedida().getDescricao());
	}

	public List<FsoConveniosFinanceiro> pesquisarConv(String parametro) {
		String paramString = (String) parametro;
		Set<FsoConveniosFinanceiro> result = new HashSet<FsoConveniosFinanceiro>();
		if ((convenio == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String.valueOf(convenio.getCodigo())) || StringUtils
						.equalsIgnoreCase(paramString, String.valueOf(convenio.getDescricao())))) {
			try {
				result = new HashSet<FsoConveniosFinanceiro>(orcamentoFacade.listarFsoConveniosAtiva(paramString));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(convenio);
		}
		List<FsoConveniosFinanceiro> resultReturn = new ArrayList<FsoConveniosFinanceiro>(result);
		// Collections.sort(resultReturn, materialComparator);
		return resultReturn;
	}

	/**
	 * Gets the cpf cnpj.
	 * 
	 * @return the cpf cnpj
	 */
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

	public void doMultiply() {
		if (this.itemContrato == null || this.itemContrato.getQuantidade() == null || this.itemContrato.getVlUnitario() == null) {
			this.itemContrato.setVlTotal(BigDecimal.ZERO);
		} else {
			this.itemContrato.setVlTotal(this.itemContrato.getVlUnitario().multiply(
					new BigDecimal(this.itemContrato.getQuantidade())));
		}
	}

	private int findNextNrItem() {
		int x = 1;
		if (this.itens != null && this.itens.size() > 0) {
			for (ScoItensContrato it : this.itens) {
				if (it.getNrItem() > x) {
					x = it.getNrItem();
				}
			}
			return x + 1;
		}
		return x;
	}

	/**
	 * Gets the item contrato.
	 * 
	 * @return the item contrato
	 */
	public ScoItensContrato getItemContrato() {
		return itemContrato;
	}

	/**
	 * Sets the item contrato.
	 * 
	 * @param itemContrato
	 *            the new item contrato
	 */
	public void setItemContrato(ScoItensContrato itemContrato) {
		this.itemContrato = itemContrato;
	}

	/**
	 * Gets the itens.
	 * 
	 * @return the itens
	 */
	public List<ScoItensContrato> getItens() {
		return itens;
	}

	/**
	 * Sets the itens.
	 * 
	 * @param itens
	 *            the new itens
	 */
	public void setItens(List<ScoItensContrato> itens) {
		this.itens = itens;
	}

	/**
	 * Gets the checks if is edit.
	 * 
	 * @return the checks if is edit
	 */
	public Boolean getIsEdit() {
		return isEdit;
	}

	/**
	 * Sets the checks if is edit.
	 * 
	 * @param isEdit
	 *            the new checks if is edit
	 */
	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	/**
	 * Gets the sco contrato.
	 * 
	 * @return the sco contrato
	 */
	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	/**
	 * Sets the sco contrato.
	 * 
	 * @param scoContrato
	 *            the new sco contrato
	 */
	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}

	/**
	 * Gets the material selecionado.
	 * 
	 * @return the material selecionado
	 */
	public ScoMaterial getMaterialSelecionado() {
		return materialSelecionado;
	}

	/**
	 * Sets the material selecionado.
	 * 
	 * @param materialSelecionado
	 *            the new material selecionado
	 */
	public void setMaterialSelecionado(ScoMaterial materialSelecionado) {
		this.materialSelecionado = materialSelecionado;
	}

	/**
	 * The Class MaterialComparator.
	 */
	private static class MaterialComparator implements Comparator<ScoMaterial> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ScoMaterial e1, ScoMaterial e2) {
			return e1.getNome().compareToIgnoreCase(e2.getNome());
		}
	}

	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public ScoMaterial getMaterial() {
		return material;
	}

	/**
	 * Sets the material.
	 * 
	 * @param material
	 *            the new material
	 */
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	/**
	 * Gets the servico selecionado.
	 * 
	 * @return the servico selecionado
	 */
	public ScoServico getServicoSelecionado() {
		return servicoSelecionado;
	}

	/**
	 * Sets the servico selecionado.
	 * 
	 * @param servicoSelecionado
	 *            the new servico selecionado
	 */
	public void setServicoSelecionado(ScoServico servicoSelecionado) {
		this.servicoSelecionado = servicoSelecionado;
	}

	/**
	 * Gets the servico.
	 * 
	 * @return the servico
	 */
	public ScoServico getServico() {
		return servico;
	}

	/**
	 * Sets the servico.
	 * 
	 * @param servico
	 *            the new servico
	 */
	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoMarcaComercial getMarcaSelecionada() {
		return marcaSelecionada;
	}

	public void setMarcaSelecionada(ScoMarcaComercial marcaSelecionada) {
		this.marcaSelecionada = marcaSelecionada;
	}

	public ScoMarcaComercial getMarca() {
		return marca;
	}

	public void setMarca(ScoMarcaComercial marca) {
		this.marca = marca;
	}

	public DominioTipoCadastroItemContrato getTipoCadastro() {
		return tipoCadastro;
	}

	public void setTipoCadastro(DominioTipoCadastroItemContrato tipoCadastro) {
		this.tipoCadastro = tipoCadastro;
	}

	public FsoConveniosFinanceiro getConvenio() {
		return convenio;
	}

	public void setConvenio(FsoConveniosFinanceiro convenio) {
		this.convenio = convenio;
	}

	public ScoItensContrato getPopupItem() {
		return popupItem;
	}

	public void setPopupItem(ScoItensContrato popupItem) {
		this.popupItem = popupItem;
	}

	public void setContSeq(String contSeq) {
		this.contSeq = contSeq;
	}

	public String getContSeq() {
		return contSeq;
	}

	public Integer getItemSeq() {
		return itemSeq;
	}

	public void setItemSeq(Integer itemSeq) {
		this.itemSeq = itemSeq;
	}

	public boolean isSetFieldsToReadonly() {
		return setFieldsToReadonly;
	}

	public void setSetFieldsToReadonly(boolean setFieldsToReadonly) {
		this.setFieldsToReadonly = setFieldsToReadonly;
	}

}
