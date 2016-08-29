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
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTxtDiagLacunas;
import br.gov.mec.aghu.core.action.ActionController;


public class DiagnosticoLaudoUnicoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 2934858652559852683L;
	
	private static final String TAG_QUEBRA = "\n";
	
	private static final String TAG_QUEBRA_BR = "<br />";	

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private String textoDiagnostico;
	private AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags;
	private AelTextoPadraoDiags aelTextoPadraoDiags;
	private String dsAelTextoPadraoDiags;
	private Boolean fecharModal;

	private List<AelGrpDiagLacunas> listaAelGrpDiagLacunas;

	private String[] valuesCombo;
	
	public class OrdenarGrupoDiagnostico implements Comparator<AelGrpDiagLacunas>{
		@Override
		 public int compare(AelGrpDiagLacunas o1, AelGrpDiagLacunas o2) {
			try {
			Integer g1 = Integer.valueOf(StringUtils.remove(o1.getLacuna(), "#"));
			Integer g2 = Integer.valueOf(StringUtils.remove(o2.getLacuna(), "#"));
				return (g1>g2 ? 1 : (g1==g2 ? 0 : -1));
			}
			catch(Exception e) {
				return 0;
			}
		}
	}
	
	public void inicio(){
		clearAelTextoPadraoDiags();
		RequestContext.getCurrentInstance().execute("PF('modalDiagnosticoWG').show()");
	}
	
	public List<AelGrpTxtPadraoDiags> pesquisarAelGrpTxtPadraoDiags(final String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelGrpTxtPadraoDiags((String) filtro, DominioSituacao.A),pesquisarAelGrpTxtPadraoDiagsCount(filtro));
	}

	public Long pesquisarAelGrpTxtPadraoDiagsCount(final String filtro) {
		return examesPatologiaFacade.pesquisarAelGrpTxtPadraoDiagsCount((String) filtro, DominioSituacao.A);
	}
	
	public List<AelTextoPadraoDiags> pesquisarTextoPadraoDiaguinostico(final String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiags.getSeq(), (String) filtro, DominioSituacao.A),pesquisarTextoPadraoDiaguinosticoCount(filtro));
	}
	
	public Long pesquisarTextoPadraoDiaguinosticoCount(final String filtro) {
		return examesPatologiaFacade.pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiagsCount(aelGrpTxtPadraoDiags.getSeq(), (String) filtro, DominioSituacao.A);
	}
	

	public List<AelTxtDiagLacunas> getAelGrpDiagLacunasItens(AelGrpDiagLacunas aelGrpMacroLacuna){
		return examesPatologiaFacade.pesquisarAelTxtDiagLacunasPorAelGrpDiagLacunas(aelGrpMacroLacuna, DominioSituacao.A);
	}
	
	public void ajustaTextoDiagnostico(final String valuComboPai, int column){
		
		
		if(dsAelTextoPadraoDiags != null && dsAelTextoPadraoDiags.indexOf('#') >= 0){
			String item = valuesCombo[column];

			dsAelTextoPadraoDiags = examesPatologiaFacade.replaceSustenidoLaudoUnico(dsAelTextoPadraoDiags, valuComboPai, item);
		}
	}
	
	public void initCombos() {
		if(aelTextoPadraoDiags != null){
			listaAelGrpDiagLacunas = examesPatologiaFacade.pesquisarAelGrpDiagLacunasPorTextoPadraoDiags(aelTextoPadraoDiags.getId().getLuhSeq(), aelTextoPadraoDiags.getId().getSeqp(), DominioSituacao.A);
			dsAelTextoPadraoDiags = aelTextoPadraoDiags.getDescricao();
			valuesCombo = new String[listaAelGrpDiagLacunas.size()];
			Collections.sort(listaAelGrpDiagLacunas, new OrdenarGrupoDiagnostico());
		} else {
			clearAelTextoPadraoDiags();
		}
	}
	
	public void clearAelTextoPadraoDiags(){
		valuesCombo = null;
		aelTextoPadraoDiags = null;
		dsAelTextoPadraoDiags = null;
		listaAelGrpDiagLacunas = null;
	}
	
	public void adicionarDiagnosticoModal(){ 
		if (!StringUtils.isEmpty(dsAelTextoPadraoDiags)) {
			 
			if (!StringUtils.isEmpty(textoDiagnostico)) {
				textoDiagnostico = textoDiagnostico + (aghuFacade.isHCPA() ? TAG_QUEBRA : TAG_QUEBRA_BR) + dsAelTextoPadraoDiags;
			}
			else {
				textoDiagnostico = dsAelTextoPadraoDiags;
			}

			clearAelTextoPadraoDiags();
	
			aelGrpTxtPadraoDiags = null;
			aelTextoPadraoDiags = null;
		}
	}

	public String getTextoDiagnostico() {
		return textoDiagnostico;
	}

	public void setTextoDiagnostico(String textoDiagnostico) {
		this.textoDiagnostico = textoDiagnostico;
	}

	public AelGrpTxtPadraoDiags getAelGrpTxtPadraoDiags() {
		return aelGrpTxtPadraoDiags;
	}

	public void setAelGrpTxtPadraoDiags(AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		this.aelGrpTxtPadraoDiags = aelGrpTxtPadraoDiags;
	}

	public AelTextoPadraoDiags getAelTextoPadraoDiags() {
		return aelTextoPadraoDiags;
	}

	public void setAelTextoPadraoDiags(AelTextoPadraoDiags aelTextoPadraoDiags) {
		this.aelTextoPadraoDiags = aelTextoPadraoDiags;
	}

	public String getDsAelTextoPadraoDiags() {
		return dsAelTextoPadraoDiags;
	}

	public void setDsAelTextoPadraoDiags(String dsAelTextoPadraoDiags) {
		this.dsAelTextoPadraoDiags = dsAelTextoPadraoDiags;
	}

	public Boolean getFecharModal() {
		return fecharModal;
	}

	public void setFecharModal(Boolean fecharModal) {
		this.fecharModal = fecharModal;
	}

	public List<AelGrpDiagLacunas> getListaAelGrpDiagLacunas() {
		return listaAelGrpDiagLacunas;
	}

	public void setListaAelGrpDiagLacunas(
			List<AelGrpDiagLacunas> listaAelGrpDiagLacunas) {
		this.listaAelGrpDiagLacunas = listaAelGrpDiagLacunas;
	}

	public String[] getValuesCombo() {
		return valuesCombo;
	}

	public void setValuesCombo(String[] valuesCombo) {
		this.valuesCombo = valuesCombo;
	}
}