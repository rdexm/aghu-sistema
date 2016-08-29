package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatPossibilidadeRealizado;
import br.gov.mec.aghu.model.FatPossibilidadeRealizadoId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroPossibilidadesFaturamentoController extends
		ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8677813867530960911L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatItensProcedHospitalar itemProcedimentoHospitalar;

	private FatPossibilidadeRealizado possibilidadeRealizado;

	private FatItensProcedHospitalar tabelaAssociada;

	private List<FatPossibilidadeRealizado> listaAuxPossibilidade = new ArrayList<FatPossibilidadeRealizado>();

	private List<SelectItem> listaComboPossibilidade = new ArrayList<SelectItem>(
			0);

	private Integer possibilidade;

	private String voltarPara;

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		if (possibilidadeRealizado == null) {
			possibilidadeRealizado = new FatPossibilidadeRealizado();
			possibilidadeRealizado.setId(new FatPossibilidadeRealizadoId());
		}
		tabelaAssociada = null;
		montarComboPossibilidade();
	}

	/**
	 * Método que monta o combo Possibilidade.
	 */
	private void montarComboPossibilidade() {
		this.listaAuxPossibilidade = this.faturamentoFacade
				.listarProcedimentosPossibilidadesAssociados(itemProcedimentoHospitalar);

		int numPossibilidade = 0;
		listaComboPossibilidade = new ArrayList<SelectItem>();
		for (FatPossibilidadeRealizado fatPossibilidadeRealizado : listaAuxPossibilidade) {
			if (fatPossibilidadeRealizado.getId().getPossibilidade() > numPossibilidade) {
				numPossibilidade = fatPossibilidadeRealizado.getId()
						.getPossibilidade();
			}
		}

		listaComboPossibilidade = new ArrayList<SelectItem>(numPossibilidade);
		for (int i = 0; i <= numPossibilidade; i++) {
			SelectItem item = new SelectItem();
			item.setDescription(String.valueOf(i + 1));
			item.setLabel(String.valueOf(i + 1));
			item.setValue(i + 1);
			listaComboPossibilidade.add(item);
		}

	}

	/**
	 * Método que retorna lista de itens para preenchimento do combo.
	 * 
	 * @return
	 */
	public List<SelectItem> comboPossibilidade() {
		return listaComboPossibilidade;
	}

	/**
	 * Método que realiza a ação do botão voltar
	 */
	public String voltar() {
		final String retorno = this.voltarPara;
		this.itemProcedimentoHospitalar = new FatItensProcedHospitalar();
		this.voltarPara = null;
		possibilidade = null;
		tabelaAssociada = null;
		return retorno;
	}

	/**
	 * Método que adiciona possibilidade no banco de dados.
	 * 
	 * @return
	 */
	public String adicionarPossibilidade() {

		// Tabela Pai
		possibilidadeRealizado.getId().setIphPhoSeq(
				itemProcedimentoHospitalar.getId().getPhoSeq());
		possibilidadeRealizado.getId().setIphSeq(
				itemProcedimentoHospitalar.getId().getSeq());

		// Associando tabela
		possibilidadeRealizado.getId().setIphSeqPossibilita(
				tabelaAssociada.getId().getSeq());
		possibilidadeRealizado.getId().setIphPhoSeqPossibilita(
				tabelaAssociada.getId().getPhoSeq());

		// Ordinal da Possibilidade
		possibilidadeRealizado.getId().setPossibilidade(possibilidade);

		try {

			this.faturamentoFacade
					.persistirPossibilidadeRealizado(possibilidadeRealizado);
			montarComboPossibilidade();
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_ADICIONADO_POSSIBILIDADE_FATURAMENTO",
					tabelaAssociada.getDescricao());
			possibilidade = null;
			tabelaAssociada = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	/**
	 * Método que exclui possibilidade do banco de dados.
	 */
	public void excluir() {
		try {

			String possibilidade = this.possibilidadeRealizado
					.getProcedimentosHospitalar().getDescricao();
			this.faturamentoFacade
					.removerPossibilidadeRealizado(this.possibilidadeRealizado);
			montarComboPossibilidade();
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_EXCLUSAO_POSSIBILIDADE_FATURAMENTO",
					possibilidade);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/*
	 * Pesquisa SuggestionBox
	 */

	public List<FatItensProcedHospitalar> pesquisarFaturamentoItensProcedimentosHospitalares(
			String parametro) {
		return  this.returnSGWithCount(this.faturamentoFacade
				.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrder(
						parametro, itemProcedimentoHospitalar
								.getProcedimentoHospitalar().getSeq()),pesquisarFaturamentoItensProcedimentosHospitalaresCount(parametro));
	}

	public Long pesquisarFaturamentoItensProcedimentosHospitalaresCount(
			String parametro) {
		return this.faturamentoFacade
				.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqOrderCount(
						parametro, itemProcedimentoHospitalar
								.getProcedimentoHospitalar().getSeq());
	}

	/**
	 * Deve exibir todos os campos do registro que não aparecem na listagem
	 * 
	 * @param item
	 * @return
	 */
	public String obterHint(FatPossibilidadeRealizado item) {

		String descricao = "";
		if (StringUtils.isNotBlank(item.getProcedimentosHospitalar()
				.getDescricao())) {
			descricao = StringUtils.abbreviate(item
					.getProcedimentosHospitalar().getDescricao(), 50);
		}
		return descricao;
	}

	/*
	 * Getters e Setters
	 */

	public FatItensProcedHospitalar getItemProcedimentoHospitalar() {
		return itemProcedimentoHospitalar;
	}

	public void setItemProcedimentoHospitalar(
			FatItensProcedHospitalar itemProcedimentoHospitalar) {
		this.itemProcedimentoHospitalar = itemProcedimentoHospitalar;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getPossibilidade() {
		return possibilidade;
	}

	public void setPossibilidade(Integer possibilidade) {
		this.possibilidade = possibilidade;
	}

	public FatPossibilidadeRealizado getPossibilidadeRealizado() {
		return possibilidadeRealizado;
	}

	public void setPossibilidadeRealizado(
			FatPossibilidadeRealizado possibilidadeRealizado) {
		this.possibilidadeRealizado = possibilidadeRealizado;
	}

	public FatItensProcedHospitalar getTabelaAssociada() {
		return tabelaAssociada;
	}

	public void setTabelaAssociada(FatItensProcedHospitalar tabelaAssociada) {
		this.tabelaAssociada = tabelaAssociada;
	}

	public List<FatPossibilidadeRealizado> getListaAuxPossibilidade() {
		return listaAuxPossibilidade;
	}

	public void setListaAuxPossibilidade(
			List<FatPossibilidadeRealizado> listaAuxPossibilidade) {
		this.listaAuxPossibilidade = listaAuxPossibilidade;
	}

}
