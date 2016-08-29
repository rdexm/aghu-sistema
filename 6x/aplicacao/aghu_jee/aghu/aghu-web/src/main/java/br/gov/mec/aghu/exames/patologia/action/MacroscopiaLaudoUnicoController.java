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
import br.gov.mec.aghu.model.AelGrpMacroLacuna;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;
import br.gov.mec.aghu.core.action.ActionController;

public class MacroscopiaLaudoUnicoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -6877618643512956461L;

	private static final String TAG_QUEBRA = "\n";
	
	private static final String TAG_QUEBRA_BR = "<br />";	

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private String textoMacroscopia;
	private AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro;
	private AelTextoPadraoMacro aelTextoPadraoMacro;
	private String dsAelTextoPadraoMacro;
	private Boolean fecharModal;

	private List<AelGrpMacroLacuna> listaAelGrpMacroLacuna;

	private String[] valuesCombo;

	public class OrdenarGrupoMacro implements Comparator<AelGrpMacroLacuna> {
		@Override
		public int compare(AelGrpMacroLacuna o1, AelGrpMacroLacuna o2) {
			try {
				Integer g1 = Integer.valueOf(StringUtils.remove(o1.getLacuna(),
						"#"));
				Integer g2 = Integer.valueOf(StringUtils.remove(o2.getLacuna(),
						"#"));
				return (g1 > g2 ? 1 : (g1 == g2 ? 0 : -1));
			} catch (Exception e) {
				return 0;
			}
		}
	}

	public void inicio() {
		clearAelTextoPadraoMacro();
		RequestContext.getCurrentInstance().execute("PF('modalMacroscopiaWG').show()");
	}

	// Métodos Modal Macroscopia:
	public List<AelGrpTxtPadraoMacro> pesquisarGrupoTextoPadraoMacro(
			final String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarGrupoTextoPadraoMacro(
				(String) filtro, DominioSituacao.A),pesquisarGrupoTextoPadraoMacroCount(filtro));
	}

	public Long pesquisarGrupoTextoPadraoMacroCount(final String filtro) {
		return examesPatologiaFacade.pesquisarGrupoTextoPadraoMacroCount(
				(String) filtro, DominioSituacao.A);
	}

	public List<AelTextoPadraoMacro> pesquisarTextoPadraoMacroscopia(
			final String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarTextoPadraoMacroscopia(
				aelGrpTxtPadraoMacro, (String) filtro, DominioSituacao.A),pesquisarTextoPadraoMacroscopiaCount(filtro));
	}

	public Long pesquisarTextoPadraoMacroscopiaCount(final String filtro) {
		return examesPatologiaFacade.pesquisarTextoPadraoMacroscopiaCount(
				aelGrpTxtPadraoMacro, (String) filtro, DominioSituacao.A);
	}

	public List<AelTxtMacroLacuna> getAelGrpMacroLacunaItens(
			AelGrpMacroLacuna aelGrpMacroLacuna) {
		return examesPatologiaFacade.pesquisarAelTxtMacroLacunaPorAelGrpMacroLacuna(aelGrpMacroLacuna, DominioSituacao.A);
	}

	public void ajustaTextoMacroscopia(final String valuComboPai, int column) {
		if (dsAelTextoPadraoMacro != null
				&& dsAelTextoPadraoMacro.indexOf('#') >= 0) {
			String item = valuesCombo[column];

			dsAelTextoPadraoMacro = examesPatologiaFacade
					.replaceSustenidoLaudoUnico(dsAelTextoPadraoMacro,
							valuComboPai, item);
		}
	}

	public void initCombos() {
		if (aelTextoPadraoMacro != null) {
			listaAelGrpMacroLacuna = examesPatologiaFacade.pesquisarAelGrpMacroLacunaPorTextoPadraoMacro(aelTextoPadraoMacro.getId().getLubSeq(), aelTextoPadraoMacro.getId().getSeqp(), DominioSituacao.A);
			dsAelTextoPadraoMacro = aelTextoPadraoMacro.getDescricao();
			valuesCombo = new String[listaAelGrpMacroLacuna.size()];
			Collections.sort(listaAelGrpMacroLacuna, new OrdenarGrupoMacro());

		} else {
			clearAelTextoPadraoMacro();
		}
	}

	public void clearAelTextoPadraoMacro() {
		valuesCombo = null;
		aelTextoPadraoMacro = null;
		dsAelTextoPadraoMacro = null;
		listaAelGrpMacroLacuna = null;
	}

	public void adicionarMacroscopiaModal() {
		if (!StringUtils.isEmpty(dsAelTextoPadraoMacro)) {

			if (!StringUtils.isEmpty(textoMacroscopia)) {
				textoMacroscopia = textoMacroscopia + (aghuFacade.isHCPA() ? TAG_QUEBRA : TAG_QUEBRA_BR) + dsAelTextoPadraoMacro;
			}
			else {
				textoMacroscopia = dsAelTextoPadraoMacro;
			}
			
			clearAelTextoPadraoMacro();

			aelGrpTxtPadraoMacro = null;
			aelTextoPadraoMacro = null;
		}
	}

	public IExamesPatologiaFacade getExamesPatologiaFacade() {
		return examesPatologiaFacade;
	}

	public void setExamesPatologiaFacade(
			IExamesPatologiaFacade examesPatologiaFacade) {
		this.examesPatologiaFacade = examesPatologiaFacade;
	}

	public String getTextoMacroscopia() {
		return textoMacroscopia;
	}

	public void setTextoMacroscopia(String textoMacroscopia) {
		this.textoMacroscopia = textoMacroscopia;
	}

	public AelGrpTxtPadraoMacro getAelGrpTxtPadraoMacro() {
		return aelGrpTxtPadraoMacro;
	}

	public void setAelGrpTxtPadraoMacro(
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) {
		this.aelGrpTxtPadraoMacro = aelGrpTxtPadraoMacro;
	}

	public AelTextoPadraoMacro getAelTextoPadraoMacro() {
		return aelTextoPadraoMacro;
	}

	public void setAelTextoPadraoMacro(AelTextoPadraoMacro aelTextoPadraoMacro) {
		this.aelTextoPadraoMacro = aelTextoPadraoMacro;
	}

	public String getDsAelTextoPadraoMacro() {
		return dsAelTextoPadraoMacro;
	}

	public void setDsAelTextoPadraoMacro(String dsAelTextoPadraoMacro) {
		this.dsAelTextoPadraoMacro = dsAelTextoPadraoMacro;
	}

	public Boolean getFecharModal() {
		return fecharModal;
	}

	public void setFecharModal(Boolean fecharModal) {
		this.fecharModal = fecharModal;
	}

	public List<AelGrpMacroLacuna> getListaAelGrpMacroLacuna() {
		return listaAelGrpMacroLacuna;
	}

	public void setListaAelGrpMacroLacuna(
			List<AelGrpMacroLacuna> listaAelGrpMacroLacuna) {
		this.listaAelGrpMacroLacuna = listaAelGrpMacroLacuna;
	}

	public String[] getValuesCombo() {
		return valuesCombo;
	}

	public void setValuesCombo(String[] valuesCombo) {
		this.valuesCombo = valuesCombo;
	}
}