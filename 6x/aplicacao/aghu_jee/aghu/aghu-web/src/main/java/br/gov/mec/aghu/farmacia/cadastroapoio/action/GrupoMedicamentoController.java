package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoMedicamentoController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	private static final long serialVersionUID = -1179705401148132069L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private GrupoMedicamentoPaginatorController grupoMedicamentoPaginatorController;

	private AfaGrupoMedicamento grupoMedicamento;
	private Boolean edicao;
	private Boolean editado;
	private Integer codigoMedicamento = null;
	private MedicamentoVO medicamentoVO;
	private DominioSituacao situacao;
	private int indice = 0;

	DominioSituacaoMedicamento[] mdtoAtivoInativo = new DominioSituacaoMedicamento[] {DominioSituacaoMedicamento.A, DominioSituacaoMedicamento.I};

	public String confirmar() {
		
		grupoMedicamentoPaginatorController.getDataModel().reiniciarPaginator();
		
		this.edicao = grupoMedicamento.getSeq() != null;
		try {
			this.farmaciaFacade.persistirAfaGrupoMedicamento(this.grupoMedicamento);

			if (!edicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRUPO_MEDICAMENTO", this.grupoMedicamento.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_MEDICAMENTO", this.grupoMedicamento.getDescricao());
			}
			edicao = Boolean.FALSE;
			editado = Boolean.FALSE;
			this.limpar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return "grupoMedicamentoList";
	}
	
	public void limpar(){
		grupoMedicamento = null;
		codigoMedicamento = null;
		medicamentoVO = null;
		situacao = null;
	}

	public void adicionarMedicamento() {
		boolean validou = true;

		final String labelMedicamento = WebUtil.initLocalizedMessage("LABEL_MEDICAMENTO", null);
		final String labelSituacao = WebUtil.initLocalizedMessage("LABEL_IND_SITUACAO", null);
 
		if (this.medicamentoVO == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, labelMedicamento, "medicamentoVO");
			validou = false;
		} 
		if (this.situacao == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, labelSituacao, "situacaoMedicamento");
			validou = false;
		} 
		
		if(validou) {
			boolean isCadastrado = false;
			this.edicao = false;
			this.codigoMedicamento = null;

			if (!grupoMedicamento.getItensGruposMedicamento().isEmpty()) {
				for (AfaItemGrupoMedicamento item : grupoMedicamento.getItensGruposMedicamento()) {
					if (item.getMedicamento().getMatCodigo().equals(this.medicamentoVO.getMedicamento().getMatCodigo())) {
						apresentarMsgNegocio(Severity.ERROR, "Medicamento " + this.medicamentoVO.getDescricaoMat() + " já adicionado.");
						isCadastrado = true;
						this.medicamentoVO = null;
						this.situacao = null;
						break;
					}
				}
			}

			if (!isCadastrado) {
				AfaItemGrupoMedicamento itemGrupoMedicamento = new AfaItemGrupoMedicamento();

				itemGrupoMedicamento.setMedicamento(farmaciaFacade.obterMedicamentoComUnidadeMedidaMedica(medicamentoVO.getMatCodigo()));
				itemGrupoMedicamento.setGrupoMedicamento(this.grupoMedicamento);
				itemGrupoMedicamento.setSituacao(this.situacao);

				grupoMedicamento.getItensGruposMedicamento().add(itemGrupoMedicamento);

				this.medicamentoVO = null;
				this.situacao = null;
			}
		}
	}

	public void alterarMedicamento() {
		if (this.medicamentoVO == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Medicamento");
		} else if (this.situacao == null) {
			apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, "Situação");
		} else {
			this.edicao = false;
			this.codigoMedicamento = null;
			editado=true;

			boolean isCadastrado = false;
			int indiceAux = 0;
			this.edicao = false;

			if (!grupoMedicamento.getItensGruposMedicamento().isEmpty()) {
				for (AfaItemGrupoMedicamento item : grupoMedicamento.getItensGruposMedicamento()) {
					indiceAux++;
					if (item.getMedicamento().getMatCodigo().equals(
							medicamentoVO.getMedicamento().getMatCodigo())
							&& this.indice != indiceAux) {
						apresentarMsgNegocio(Severity.ERROR, "Medicamento " + medicamentoVO.getMedicamento() + " já adicionado.");
						isCadastrado = true;
						break;
					}
				}
			}

			if (!isCadastrado) {
				AfaItemGrupoMedicamento itemGrupoMedicamento = grupoMedicamento.getItensGruposMedicamento().get(this.indice - 1);

				itemGrupoMedicamento.setMedicamento(this.medicamentoVO
						.getMedicamento());
				itemGrupoMedicamento.setGrupoMedicamento(this.grupoMedicamento);
				itemGrupoMedicamento.setSituacao(this.situacao);

				this.medicamentoVO = null;
				this.situacao = null;
			}
		}
	}

	public void editarMedicamento(AfaItemGrupoMedicamento item) {
		this.edicao = true;
		this.indice = 0;
		this.codigoMedicamento = item.getMedicamento().getMatCodigo();

		for (Iterator<AfaItemGrupoMedicamento> i = grupoMedicamento.getItensGruposMedicamento().iterator(); i.hasNext();) {
			AfaItemGrupoMedicamento itemGrupo = i.next();
			this.indice++;
			if (itemGrupo.getMedicamento().getMatCodigo().equals(item.getMedicamento().getMatCodigo())) {
				this.medicamentoVO = new MedicamentoVO();
				this.medicamentoVO.setMatCodigo(itemGrupo.getMedicamento().getMatCodigo());
				this.medicamentoVO.setDescricaoEditada(itemGrupo.getMedicamento().getDescricaoEditada());
				this.medicamentoVO.setMedicamento(itemGrupo.getMedicamento());
				this.situacao = itemGrupo.getSituacao();

				break;
			}
		}
	}

	public void removerMedicamento(AfaItemGrupoMedicamento item) {
		this.edicao = false;
		this.codigoMedicamento = null;
		for (Iterator<AfaItemGrupoMedicamento> i = grupoMedicamento.getItensGruposMedicamento().iterator(); i.hasNext();) {
			AfaItemGrupoMedicamento itemGrupo = i.next();
			if (itemGrupo.getMedicamento().getMatCodigo().equals(
					item.getMedicamento().getMatCodigo())) {
				i.remove();
				break;
			}
		}
	}

	public void cancelarEdicaoMedicamento() {
		this.edicao = false;
		this.codigoMedicamento = null;
		this.medicamentoVO = null;
		this.situacao = null;
	}

	public List<MedicamentoVO> obterMedicamentosVO(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosVO(strPesquisa,null,mdtoAtivoInativo, null, false),obterMedicamentosVOCount(strPesquisa));
	}

	public Long obterMedicamentosVOCount(String strPesquisa) {
		return this.farmaciaFacade.obterMedicamentosVOCount(strPesquisa,null,mdtoAtivoInativo, null, false);
	}

	public String cancelar() {
		this.limpar();
		return "grupoMedicamentoList";
	}

	public AfaGrupoMedicamento getGrupoMedicamento() {
		return grupoMedicamento;
	}

	public void setGrupoMedicamento(AfaGrupoMedicamento grupoMedicamento) {
		this.grupoMedicamento = grupoMedicamento;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public MedicamentoVO getMedicamentoVO() {
		return medicamentoVO;
	}

	public void setMedicamentoVO(MedicamentoVO medicamentoVO) {
		this.medicamentoVO = medicamentoVO;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public Integer getCodigoMedicamento() {
		return codigoMedicamento;
	}

	public void setCodigoMedicamento(Integer codigoMedicamento) {
		this.codigoMedicamento = codigoMedicamento;
	}

	public Boolean getEditado() {
		return editado;
	}

	public void setEditado(Boolean editado) {
		this.editado = editado;
	}
}
