package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;



public class ConsultaDocumentosCertificadosPOLController extends ActionController {
	private static final long serialVersionUID = -5690432078701647894L;
	private static final String PAGE_VISUALIZAR_DOC_CERTIF="pol-visualizarDocumentoCertificado";
	

	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final Comparator PT_BR_COMPARATOR = new Comparator() {		
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt",
					"BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable) o1).compareTo(o2);
		}
	};
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;

	private List<AghVersaoDocumento> lista = new ArrayList<AghVersaoDocumento>();
	private Integer prontuario;
	private Comparator<AghVersaoDocumento> currentComparator;
	private String currentSortProperty;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}	
	
	public void inicio() {
		 
		if (itemPOL!=null){
			prontuario = itemPOL.getProntuario();
		}
		
		pesquisar();
	}

	public String abrirVisualizarDocumentoCertificado(){
		return PAGE_VISUALIZAR_DOC_CERTIF;
	}
	
	
	public void pesquisar() {
		AipPacientes paciente = this.pacienteFacade
				.obterPacientePorProntuario(prontuario);
		this.lista = this.certificacaoDigitalFacade
				.pesquisarDocumentosDoPaciente(paciente);		
	}
	
	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {
		@SuppressWarnings("rawtypes")
		Comparator comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(
					PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.lista, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
		
	}
	
	public String realizarChamada(String target){
		return target;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public List<AghVersaoDocumento> getLista() {
		return lista;
	}

	public void setLista(List<AghVersaoDocumento> lista) {
		this.lista = lista;
	}

}
