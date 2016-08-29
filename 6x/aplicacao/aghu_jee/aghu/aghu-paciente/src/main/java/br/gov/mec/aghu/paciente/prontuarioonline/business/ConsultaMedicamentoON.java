package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.VAipPolMdtos;
import br.gov.mec.aghu.paciente.dao.VAipPolMdtosDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ConsultaMedicamentoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaMedicamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private VAipPolMdtosDAO vAipPolMdtosDAO;
	
	private static final long serialVersionUID = -1456500041237319652L;
	
	private static final Comparator<VAipPolMdtos> MEDICAMENTOS_POL_COMPARATOR = new Comparator<VAipPolMdtos>() {

		@Override
		public int compare(VAipPolMdtos o1, VAipPolMdtos o2) {

			if (o1.getDataInicioSemHora() != null
					&& o2.getDataInicioSemHora() != null
					&& o1.getDataInicioSemHora().after(
							o2.getDataInicioSemHora())) {
				return -1;
			} else if (o1.getDataInicioSemHora() != null
					&& o2.getDataInicioSemHora() != null
					&& o1.getDataInicioSemHora().before(
							o2.getDataInicioSemHora())) {
				return 1;
			} else {
				if (!o1.getMedicamento().equalsIgnoreCase(o2.getMedicamento())) {
					return o1.getMedicamento().compareToIgnoreCase(
							o2.getMedicamento());
				} else {
					if (o1.getDataFimSemHora().after(o2.getDataFimSemHora())) {
						return -1;
					} else if (o1.getDataFimSemHora().before(
							o2.getDataFimSemHora())) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		}
	};

	public List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo, Date dataInicio, int firstResult, int maxResults) {
		List<VAipPolMdtos> medicamentos = this.getVAipPolMdtosDAO().obterMedicamentos(codPaciente, tipo, dataInicio, firstResult, maxResults);

		for (VAipPolMdtos medicamento : medicamentos) {
			if (medicamento.getMedicamento() == null || medicamento.getMedicamento().isEmpty()) {
				medicamento.setMedicamento(
									obterMedicamento(medicamento.getId().getImePmdAtdSeq(), medicamento.getId().getImePmdSeq())
										.getDescricaoFormatadaSemObservacao(false));
			}
		}
		
		Collections.sort(medicamentos, MEDICAMENTOS_POL_COMPARATOR);

		return medicamentos;
	}
	
	public List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo, Date dataInicio) {
		List<VAipPolMdtos> medicamentos = this.getVAipPolMdtosDAO().obterMedicamentos(codPaciente, tipo, dataInicio);

		for (VAipPolMdtos medicamento : medicamentos) {
			if (medicamento.getMedicamento() == null || medicamento.getMedicamento().isEmpty()) {
				medicamento.setMedicamento(
									obterMedicamento(medicamento.getId().getImePmdAtdSeq(), medicamento.getId().getImePmdSeq())
										.getDescricaoFormatadaSemObservacao(false));
			}
		}
		
		Collections.sort(medicamentos, MEDICAMENTOS_POL_COMPARATOR);

		return medicamentos;
	}
	

	private MpmPrescricaoMdto obterMedicamento(Integer imePmdAtdSeq, Long imePmdSeq) {
		return this.getPrescricaoMedicaFacade().obterPrescricaoMedicamento(imePmdAtdSeq, imePmdSeq);
	}
	
	// Quantidade de milissegundos em um dia
	private static final Integer TEMPO_DIA = 1000 * 60 * 60 * 24;
	
	/**
	 * ORADB: Function MPMC_QTDE_MDTO_POL
	 */
	public Integer obterQuantidadeMedicamentos(Integer atdSeq, String descMedicamento) {


		Calendar data = Calendar.getInstance();
		Date dthrFim = aghuFacade.obterDataFimAtendimento(atdSeq);

		List<MpmPrescricaoMdto> listaPolMdtosCompleta = prescricaoMedicaFacade.pesquisaMedicamentosPOL(atdSeq, DominioIndPendenteItemPrescricao.N);
		List<MpmPrescricaoMdto> listaPolMdtos = new ArrayList<MpmPrescricaoMdto>();

		for (MpmPrescricaoMdto medicamento : listaPolMdtosCompleta) {
			if (descMedicamento.equals(medicamento.getDescricaoFormatadaSemObservacao(false))) {
				listaPolMdtos.add(medicamento);
			}
		}

		List<Calendar> tabDatas = new ArrayList<Calendar>();
		Float diferencaDias = 0F;

		for (MpmPrescricaoMdto polMdto : listaPolMdtos) {

			Calendar dataInicio = getCalendarSemHora(polMdto.getDthrInicio());
			Calendar dataFim = Calendar.getInstance();

			if (polMdto.getDthrFim() == null) {
				if (dthrFim != null) {
					dataFim = getCalendarSemHora(dthrFim);
				} else {
					dataFim = getCalendarSemHora(new Date());
				}
			} else {
				dataFim = getCalendarSemHora(polMdto.getDthrFim());
			}

			float diferenca = dataFim.getTimeInMillis()
					- dataInicio.getTimeInMillis();

			diferencaDias = diferenca / TEMPO_DIA + 1;

			data = dataInicio;

			for (Integer indice1 = 0; indice1 < diferencaDias; indice1++) {

				if (!tabDatas.contains(data)) {
					Calendar dataAdicionar = Calendar.getInstance();
					dataAdicionar.set(data.get(Calendar.YEAR), data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH), 0, 0, 0);// = data;
					dataAdicionar.set(Calendar.MILLISECOND, 0);
					tabDatas.add(dataAdicionar);
				}
				data.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		return tabDatas.size();
	}

	private Calendar getCalendarSemHora(Date dataEntrada) {
		Calendar data = Calendar.getInstance();

		data.setTime(dataEntrada);
		data.set(data.get(Calendar.YEAR), data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		data.set(Calendar.MILLISECOND, 0);

		return data;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected VAipPolMdtosDAO getVAipPolMdtosDAO() {
		return vAipPolMdtosDAO;
	}
}