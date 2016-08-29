package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdmUnfId;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.vo.ViaAdministracaoPermitidaUnidadeVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterMultiplasViasAdministracaoPermitidasUnidadeController extends ActionController {

	private static final long serialVersionUID = -6069185589052192076L;
	
	private static final String PAGE_MANTER_VIAS_ADM_PERM_UND = "manterViasAdmPermUnd";

	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;

	@Inject
	private IFarmaciaFacade farmaciaFacade;

	private AghUnidadesFuncionaisVO unidadeFuncional;
	private List<ViaAdministracaoPermitidaUnidadeVO> vias;
	private Boolean marcarTodos;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
	 

		carregarLista();
	
	}

	private void carregarLista() {
		vias = new ArrayList<ViaAdministracaoPermitidaUnidadeVO>();
		List<AfaViaAdministracao> viasAdministracao = farmaciaFacade.listarTodasAsVias("");
		List<AfaViaAdmUnf> viasAdministracaoUnidade = farmaciaFacade.
				listarAfaViaAdmUnfPorUnidadeFuncional(new AghUnidadesFuncionais(this.unidadeFuncional.getSeq()));
		
		for (AfaViaAdministracao via : viasAdministracao) {
			ViaAdministracaoPermitidaUnidadeVO item = new ViaAdministracaoPermitidaUnidadeVO();
			item.setSigla(via.getSigla());
			item.setDescricao(via.getDescricao());
			item.setMarcarLinha(Boolean.FALSE);
			item.setSelecionadoNaBase(Boolean.FALSE);
			for (AfaViaAdmUnf viaUnidade : viasAdministracaoUnidade) {
				if (viaUnidade.getId().getVadSigla().equals(via.getSigla())) {
					item.setSelecionadoNaBase(Boolean.TRUE);
					item.setMarcarLinha(Boolean.TRUE);
				}
			}
			vias.add(item);
		}
	}

	public String gravar() {
		for (ViaAdministracaoPermitidaUnidadeVO via : vias) {
			if (via.getMarcarLinha() && !via.getSelecionadoNaBase()) {
				try {
					AfaViaAdmUnfId viaAdmId = new AfaViaAdmUnfId();
					viaAdmId.setUnfSeq(unidadeFuncional.getSeq());
					viaAdmId.setVadSigla(via.getSigla());
					AfaViaAdmUnf viaAdm = new AfaViaAdmUnf();
					viaAdm.setId(viaAdmId);
					viaAdm.setIndSituacao(DominioSituacao.A);
					farmaciaApoioFacade.gravarViaAdministracao(viaAdm, false);
				} catch (BaseException e) {
					super.apresentarExcecaoNegocio(e);
					return null;
				}
			}
		}
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_VIA_ADMIN_PERMITIDA");
		carregarLista();
		return null;
	}

	public String cancelar() {
		return PAGE_MANTER_VIAS_ADM_PERM_UND;
	}

	public AghUnidadesFuncionaisVO getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionaisVO unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public List<ViaAdministracaoPermitidaUnidadeVO> getVias() {
		return vias;
	}

	public void setVias(List<ViaAdministracaoPermitidaUnidadeVO> vias) {
		this.vias = vias;
	}

	public Boolean getMarcarTodos() {
		return marcarTodos;
	}

	public void setMarcarTodos(Boolean marcarTodos) {
		this.marcarTodos = marcarTodos;
	}

}
