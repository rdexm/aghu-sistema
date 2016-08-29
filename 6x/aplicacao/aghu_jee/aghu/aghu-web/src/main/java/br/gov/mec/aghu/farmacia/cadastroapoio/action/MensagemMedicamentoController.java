package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoMedicamentoMensagem;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MensagemMedicamentoController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	private static final long serialVersionUID = 7276313947095086854L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MensagemMedicamentoPaginatorController mensagemMedicamentoPaginatorController;

	private AfaMensagemMedicamento mensagemMedicamento;
	private Boolean edicao;
	private Short seqGrupoMedicamento = null;
	private AfaGrupoMedicamento grupoMedicamento;
	private DominioSituacao situacao;
	private List<AfaGrupoMedicamentoMensagem> listaMedicamentosGrupos;
	private int indice = 0;

	public String confirmar() {
		mensagemMedicamentoPaginatorController.getDataModel().reiniciarPaginator();

		try {
			this.edicao = mensagemMedicamento != null && mensagemMedicamento.getSeq() != null;
			mensagemMedicamento.setGruposMedicamentosMensagem(this.listaMedicamentosGrupos);
			farmaciaFacade.persistirAfaMensagemMedicamento(this.mensagemMedicamento);

			if (edicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MENSAGEM_MEDICAMENTO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MENSAGEM_MEDICAMENTO");				
			}
			this.edicao = Boolean.FALSE;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return "mensagemMedicamentoList";
	}

	public void adicionarGrupoMedicamento() {
		boolean validou = true;

		final String labelGrupoMedicamento = WebUtil.initLocalizedMessage("LABEL_GRUPO_MEDICAMENTO", null);
		final String labelSituacaoGrupo = WebUtil.initLocalizedMessage("LABEL_IND_SITUACAO", null);

		if (this.grupoMedicamento == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, labelGrupoMedicamento, "grupoMedicamento");
			validou = false;
		}

		if (this.situacao == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, labelSituacaoGrupo, "situacaoGrupoMedicamentoMensagem");
			//getStatusMessages().addToControlFromResourceBundle("situacaoGrupoMedicamentoMensagem", Severity.ERROR, "CAMPO_OBRIGATORIO", labelSituacaoGrupo);
			validou = false;
		}

		if(validou){

			boolean isCadastrado = false;
			this.edicao = false;
			this.seqGrupoMedicamento = null;

			if(listaMedicamentosGrupos == null){
				listaMedicamentosGrupos = new ArrayList<AfaGrupoMedicamentoMensagem>();
			}
			
			if (!this.listaMedicamentosGrupos.isEmpty()) {
				for (AfaGrupoMedicamentoMensagem item : this.listaMedicamentosGrupos) {
					if (item.getGrupoMedicamento().getSeq().equals(
							this.grupoMedicamento.getSeq())) {
						apresentarMsgNegocio(Severity.ERROR, "Grupo de Medicamento " + this.grupoMedicamento.getDescricao() + " já adicionado.");
						isCadastrado = true;
						this.grupoMedicamento = null;
						this.situacao = null;
						break;
					}
				}
			}

			if (!isCadastrado) {
				AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem = new AfaGrupoMedicamentoMensagem();

				grupoMedicamentoMensagem
						.setMensagemMedicamento(this.mensagemMedicamento);
				grupoMedicamentoMensagem
						.setGrupoMedicamento(this.grupoMedicamento);
				grupoMedicamentoMensagem.setSituacao(this.situacao);

				this.listaMedicamentosGrupos.add(grupoMedicamentoMensagem);

				this.grupoMedicamento = null;
				this.situacao = null;
			}
		}
	}

	public void alterarGrupoMedicamento() {
		if (this.grupoMedicamento == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Grupo de Medicamento");
		} else if (this.situacao == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Situação");
		} else {
			this.edicao = false;
			this.seqGrupoMedicamento = null;

			boolean isCadastrado = false;
			int indiceAux = 0;
			this.edicao = false;

			if (!listaMedicamentosGrupos.isEmpty()) {
				for (AfaGrupoMedicamentoMensagem item : listaMedicamentosGrupos) {
					indiceAux++;
					if (item.getGrupoMedicamento().getSeq().equals(grupoMedicamento.getSeq()) && this.indice != indiceAux) {
						apresentarMsgNegocio(Severity.ERROR,"Grupo de Medicamento " + grupoMedicamento.getDescricao() + " já adicionado.");
						isCadastrado = true;
						break;
					}
				}
			}

			if (!isCadastrado) {
				AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem = listaMedicamentosGrupos
						.get(this.indice - 1);

				grupoMedicamentoMensagem
						.setMensagemMedicamento(this.mensagemMedicamento);
				grupoMedicamentoMensagem
						.setGrupoMedicamento(this.grupoMedicamento);
				grupoMedicamentoMensagem.setSituacao(this.situacao);

				this.grupoMedicamento = null;
				this.situacao = null;
			}
		}
	}

	public void editarGrupoMedicamento(AfaGrupoMedicamentoMensagem item) {
		this.edicao = true;
		this.indice = 0;
		this.seqGrupoMedicamento = item.getGrupoMedicamento().getSeq();

		for (Iterator<AfaGrupoMedicamentoMensagem> i = listaMedicamentosGrupos
				.iterator(); i.hasNext();) {
			AfaGrupoMedicamentoMensagem itemGrupo = i.next();
			this.indice++;
			if (itemGrupo.getGrupoMedicamento().getSeq().equals(
					item.getGrupoMedicamento().getSeq())) {
				this.grupoMedicamento = itemGrupo.getGrupoMedicamento();
				this.situacao = itemGrupo.getSituacao();

				break;
			}
		}
	}

	public void removerGrupoMedicamento(AfaGrupoMedicamentoMensagem item) {
		this.edicao = false;
		this.seqGrupoMedicamento = null;
		for (Iterator<AfaGrupoMedicamentoMensagem> i = listaMedicamentosGrupos
				.iterator(); i.hasNext();) {
			AfaGrupoMedicamentoMensagem itemGrupo = i.next();
			if (itemGrupo.getGrupoMedicamento().getSeq().equals(
					item.getGrupoMedicamento().getSeq())) {
				i.remove();
				break;
			}
		}
	}

	public void cancelarEdicaoGrupoMedicamento() {
		this.edicao = false;
		this.seqGrupoMedicamento = null;
		this.grupoMedicamento = null;
		this.situacao = null;
	}

	public List<AfaGrupoMedicamento> pesquisaGruposMedicamentos(
			String objPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.pesquisaGruposMedicamentos(
				(String) objPesquisa),pesquisaGruposMedicamentosCount(objPesquisa));
	}

	public Long pesquisaGruposMedicamentosCount(String objPesquisa) {
		return this.farmaciaFacade
				.pesquisaGruposMedicamentosCount((String) objPesquisa);
	}
	
	public void limpar(){
		listaMedicamentosGrupos = new ArrayList<AfaGrupoMedicamentoMensagem>();
	}

	public String cancelar() {
		return "mensagemMedicamentoList";
	}

	public AfaMensagemMedicamento getMensagemMedicamento() {
		return mensagemMedicamento;
	}

	public void setMensagemMedicamento(AfaMensagemMedicamento mensagemMedicamento) {
		this.mensagemMedicamento = mensagemMedicamento;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Short getSeqGrupoMedicamento() {
		return seqGrupoMedicamento;
	}

	public void setSeqGrupoMedicamento(Short seqGrupoMedicamento) {
		this.seqGrupoMedicamento = seqGrupoMedicamento;
	}

	public AfaGrupoMedicamento getGrupoMedicamento() {
		return grupoMedicamento;
	}

	public void setGrupoMedicamento(AfaGrupoMedicamento grupoMedicamento) {
		this.grupoMedicamento = grupoMedicamento;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<AfaGrupoMedicamentoMensagem> getListaMedicamentosGrupos() {
		return listaMedicamentosGrupos;
	}

	public void setListaMedicamentosGrupos(
			List<AfaGrupoMedicamentoMensagem> listaMedicamentosGrupos) {
		this.listaMedicamentosGrupos = listaMedicamentosGrupos;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}
}