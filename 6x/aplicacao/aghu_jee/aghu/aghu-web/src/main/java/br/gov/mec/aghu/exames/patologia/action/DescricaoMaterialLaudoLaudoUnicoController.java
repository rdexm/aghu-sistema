package br.gov.mec.aghu.exames.patologia.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.core.action.ActionController;


public class DescricaoMaterialLaudoLaudoUnicoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}	

	private static final long serialVersionUID = -6877618643512956461L;
	
	private static final String TAG_QUEBRA = "\n";
	
	private static final String TAG_QUEBRA_BR = "<br />";	

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	

	private String textoDescricaoMaterialLaudo;
	private AelGrpTxtDescMats aelGrpTxtDescMats;
	private AelTxtDescMats aelTxtDescMats;
	private String dsAelTxtDescMats;
	private Boolean fecharModal;

	private List<AelGrpDescMatLacunas> listaAelGrpDescMatLacunas;

	private String[] valuesCombo;
	
	public class OrdenarGrupoDescMats implements Comparator<AelGrpDescMatLacunas> {
		@Override
		public int compare(AelGrpDescMatLacunas o1, AelGrpDescMatLacunas o2) {
			try {
				Integer g1 = Integer.valueOf(StringUtils.remove(o1.getLacuna(), "#"));
				Integer g2 = Integer.valueOf(StringUtils.remove(o2.getLacuna(), "#"));
				return (g1 > g2 ? 1 : (g1 == g2 ? 0 : -1));
			} catch (Exception e) {
				return 0;
			}
		}
	}
	
	public void inicio(){
		clearAelTxtDescMats();
		RequestContext.getCurrentInstance().execute("PF('modalDescMatsWG').show()");
	}
	
	// Métodos Modal DescMats
	public List<AelGrpTxtDescMats> pesquisarGrupoTextoPadraoDescMats(final String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarGrupoTextoPadraoDescMats(null, (String) filtro, DominioSituacao.A),pesquisarGrupoTextoPadraoDescMatsCount(filtro));
	}

	public Long pesquisarGrupoTextoPadraoDescMatsCount(final String filtro) {
		return examesPatologiaFacade.pesquisarGrupoTextoPadraoDescMatsCount((String) filtro, DominioSituacao.A);
	}

	public List<AelTxtDescMats> pesquisarTextoPadraoDescMats(final String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarTextoPadraoDescMats(aelGrpTxtDescMats, (String) filtro, DominioSituacao.A),pesquisarTextoPadraoDescMatsCount(filtro));
	}

	public Long pesquisarTextoPadraoDescMatsCount(final String filtro) {
		return examesPatologiaFacade.pesquisarTextoPadraoDescMatsCount(aelGrpTxtDescMats, (String) filtro, DominioSituacao.A);
	}

	public List<AelDescMatLacunas> getAelGrpDescMatLacunasItens(AelGrpDescMatLacunas aelGrpDescMatLacunas) {
		return examesPatologiaFacade.pesquisarAelDescMatLacunasPorAelGrpDescMatLacunas(aelGrpDescMatLacunas, DominioSituacao.A);
	}
	
	public void ajustaTextoDescMats(final String valuComboPai, int column) {
		if (dsAelTxtDescMats != null && dsAelTxtDescMats.indexOf('#') >= 0) {
			String item = valuesCombo[column];

			dsAelTxtDescMats = examesPatologiaFacade.replaceSustenidoLaudoUnico(dsAelTxtDescMats, valuComboPai, item);
		}
	}
	
	public void initCombos() {
		if (aelTxtDescMats != null) {
			listaAelGrpDescMatLacunas = examesPatologiaFacade.pesquisarAelGrpDescMatLacunasPorTextoPadraoDescMats(aelTxtDescMats.getId()
					.getGtmSeq(), aelTxtDescMats.getId().getSeqp(), DominioSituacao.A);
			dsAelTxtDescMats = aelTxtDescMats.getDescricao();
			valuesCombo = new String[listaAelGrpDescMatLacunas.size()];
			Collections.sort(listaAelGrpDescMatLacunas, new OrdenarGrupoDescMats());

		} else {
			clearAelTxtDescMats();
		}
	}
	
	public void clearAelTxtDescMats() {
		valuesCombo = null;
		aelTxtDescMats = null;
		dsAelTxtDescMats = null;
		listaAelGrpDescMatLacunas = null;
	}
	
	public void adicionarDescMatsModal() {
		if (!StringUtils.isEmpty(dsAelTxtDescMats)) {
			if (!StringUtils.isEmpty(textoDescricaoMaterialLaudo)) {
				textoDescricaoMaterialLaudo = textoDescricaoMaterialLaudo + (aghuFacade.isHCPA() ? TAG_QUEBRA : TAG_QUEBRA_BR) + dsAelTxtDescMats;
			}
			else {
				textoDescricaoMaterialLaudo = dsAelTxtDescMats;
			}

			clearAelTxtDescMats();

			aelGrpTxtDescMats = null;
			aelTxtDescMats = null;
		}
	}


	public IExamesPatologiaFacade getExamesPatologiaFacade() {
		return examesPatologiaFacade;
	}

	public String getTextoDescricaoMaterialLaudo() {
		return textoDescricaoMaterialLaudo;
	}

	public void setTextoDescricaoMaterialLaudo(String textoDescricaoMaterialLaudo) {
		this.textoDescricaoMaterialLaudo = textoDescricaoMaterialLaudo;
	}

	public AelGrpTxtDescMats getAelGrpTxtDescMats() {
		return aelGrpTxtDescMats;
	}

	public void setAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtDescMats) {
		this.aelGrpTxtDescMats = aelGrpTxtDescMats;
	}

	public AelTxtDescMats getAelTxtDescMats() {
		return aelTxtDescMats;
	}

	public void setAelTxtDescMats(AelTxtDescMats aelTxtDescMats) {
		this.aelTxtDescMats = aelTxtDescMats;
	}

	public String getDsAelTxtDescMats() {
		return dsAelTxtDescMats;
	}

	public void setDsAelTxtDescMats(String dsAelTxtDescMats) {
		this.dsAelTxtDescMats = dsAelTxtDescMats;
	}

	public Boolean getFecharModal() {
		return fecharModal;
	}

	public void setFecharModal(Boolean fecharModal) {
		this.fecharModal = fecharModal;
	}

	public List<AelGrpDescMatLacunas> getListaAelGrpDescMatLacunas() {
		return listaAelGrpDescMatLacunas;
	}

	public void setListaAelGrpDescMatLacunas(List<AelGrpDescMatLacunas> listaAelGrpDescMatLacunas) {
		this.listaAelGrpDescMatLacunas = listaAelGrpDescMatLacunas;
	}

	public String[] getValuesCombo() {
		return valuesCombo;
	}

	public void setValuesCombo(String[] valuesCombo) {
		this.valuesCombo = valuesCombo;
	}
}