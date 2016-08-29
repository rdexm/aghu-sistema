package br.gov.mec.aghu.emergencia.action;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.controlepaciente.vo.EcpItemControleVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteServiceVO;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ListarControlesPacienteController extends ActionController {


	private static final long serialVersionUID = -1408451175566150412L;
	private Long seqHorarioControle;
	
	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator PT_BR_COMPARATOR = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt","BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable) o1).compareTo(o2);
		}
	};
	
	
	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	private List<EcpItemControleVO> listaItensControleMn = new ArrayList<EcpItemControleVO>();
	private List<RegistroControlePacienteServiceVO> listaRegistrosControleMn = new ArrayList<RegistroControlePacienteServiceVO>();
	private Comparator<RegistroControlePacienteServiceVO> currentComparator;
	private String currentSortProperty;
	private Integer pacCodigo;
	private Long trgSeq;
	private Short unfSeq;

	private static final Integer TAM_MAXIMO_DESCRICAO = 13;
	
	
	public void excluir(Long seqHorarioControle) {
		try {
			this.emergenciaFacade.excluirRegistroControlePaciente(seqHorarioControle);
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(e.getMessage());
		}
		this.buscaMonitorizacoes();
	}
	
	public void excluir() {
		try {
			this.emergenciaFacade.excluirRegistroControlePaciente(this.seqHorarioControle);
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(e.getMessage());
		}
		this.buscaMonitorizacoes();
	}	
	
	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {
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

		Collections.sort(this.listaRegistrosControleMn, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
	}
	
	public Integer obterTamanhoColunaMn(String descricaoSiglaUnidadeMedida, int index) {
		// tamanho mínimo padrão para uma coluna qualquer.
		int tamanhoMinimo = 45;
		int auxTamanho = 0;
		if (StringUtils.isNotBlank(descricaoSiglaUnidadeMedida)) {
			// Calculo simples para identificar um tamanho dependendo da
			// quantidade de caracteres de um texto. Para cada caracter, 7.5px,
			// somados mais 5px no total.
			auxTamanho = (int) (descricaoSiglaUnidadeMedida.length() * 7.5) + 5;
		}
		
		if (auxTamanho < tamanhoMinimo) {
			auxTamanho = tamanhoMinimo;
		}
		
		// tamanho padrão para o caso de um número tipo ######,## seja informado.
		int tamanhoMinimoCampoNumericoCompleto = 60;
		int quantidadeNumerosQueExibe = 6;
		if (auxTamanho < tamanhoMinimoCampoNumericoCompleto) {
			for (RegistroControlePacienteServiceVO vo : listaRegistrosControleMn) {
				String auxValor = vo.getValor()[index];
				if (StringUtils.isNotBlank(auxValor)
						&& auxValor.length() > quantidadeNumerosQueExibe) {
					auxTamanho = tamanhoMinimoCampoNumericoCompleto;
					break;
				}
			}
		}
		
		return auxTamanho;
	}
	
	/**
	 * Informa se deve ser apresentada a tooltip nos campos de descrição
	 */
	public Boolean apresentaToolTip(Object objParam ){
		
		try {
			String strParam = (String) objParam;

			if (StringUtils.isNotBlank(strParam)
					&& !CoreUtil.isNumeroInteger(strParam)
					&& strParam.length() > TAM_MAXIMO_DESCRICAO) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		
		return false;
	}
	
	/**
	 * Utilizado para os campos de descrição cujo tamanho seja maior que a coluna apresentada no dataTable
	 */
	public String mostrarValorFormatado(Object objParam) {

		try {
			String strParam = (String) objParam;
			if (this.apresentaToolTip(objParam)) {
				return strParam.substring(0, TAM_MAXIMO_DESCRICAO) + "...";
			} else {
				return strParam;
			}
		} catch (Exception e) {
			return null;
		}
				
	}
	
	public void buscaMonitorizacoes() {
		listaItensControleMn = emergenciaFacade
				.buscarItensControlePorPacientePeriodo(pacCodigo, trgSeq);
		
		if (listaItensControleMn.isEmpty()) {
			if(!this.listaRegistrosControleMn.isEmpty()){
				this.listaRegistrosControleMn = new ArrayList<RegistroControlePacienteServiceVO>();
			}
		}
		
		try {
			this.listaRegistrosControleMn = emergenciaFacade.pesquisarRegistrosPaciente(pacCodigo, listaItensControleMn, trgSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}			
	}
	
	public Boolean isUnidadeFuncionalValida(Short unfSeq) throws ApplicationBusinessException {
		if(unfSeq != null && !unfSeq.equals(Short.valueOf("0"))) {
			return this.emergenciaFacade.validaUnidadeFuncionalInformatizada(unfSeq);
		} else {
			return this.emergenciaFacade.validaUnidadeFuncionalInformatizada(this.unfSeq);
		}
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public IEmergenciaFacade getEmergenciaFacade() {
		return emergenciaFacade;
	}

	public void setEmergenciaFacade(IEmergenciaFacade emergenciaFacade) {
		this.emergenciaFacade = emergenciaFacade;
	}

	public List<EcpItemControleVO> getListaItensControleMn() {
		return listaItensControleMn;
	}

	public void setListaItensControleMn(List<EcpItemControleVO> listaItensControleMn) {
		this.listaItensControleMn = listaItensControleMn;
	}

	public List<RegistroControlePacienteServiceVO> getListaRegistrosControleMn() {
		return listaRegistrosControleMn;
	}

	public void setListaRegistrosControleMn(
			List<RegistroControlePacienteServiceVO> listaRegistrosControleMn) {
		this.listaRegistrosControleMn = listaRegistrosControleMn;
	}

	public Comparator<RegistroControlePacienteServiceVO> getCurrentComparator() {
		return currentComparator;
	}

	public void setCurrentComparator(
			Comparator<RegistroControlePacienteServiceVO> currentComparator) {
		this.currentComparator = currentComparator;
	}

	public String getCurrentSortProperty() {
		return currentSortProperty;
	}

	public void setCurrentSortProperty(String currentSortProperty) {
		this.currentSortProperty = currentSortProperty;
	}
	
	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Long getSeqHorarioControle() {
		return seqHorarioControle;
	}

	public void setSeqHorarioControle(Long seqHorarioControle) {
		this.seqHorarioControle = seqHorarioControle;
	}

}