package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.text.SimpleDateFormat;
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
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPrescricaoMdtosHist;
import br.gov.mec.aghu.model.VAipPolMdtosAghuHist;
import br.gov.mec.aghu.paciente.dao.VAipPolMdtosAghuHistDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ConsultaMedicamentoHistON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaMedicamentoHistON.class);
	private static final long serialVersionUID = -1456500041237319652L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private VAipPolMdtosAghuHistDAO vAipPolMdtosAghuHistDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	
	private static final Comparator<VAipPolMdtosAghuHist> MEDICAMENTOS_POL_COMPARATOR = new Comparator<VAipPolMdtosAghuHist>() {

		@Override
		public int compare(VAipPolMdtosAghuHist o1, VAipPolMdtosAghuHist o2) {

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
				if (o1.getDataFimSemHora().after(o2.getDataFimSemHora())) {
					return -1;
				} else if (o1.getDataFimSemHora().before(
						o2.getDataFimSemHora())) {
					return 1;
				}else {
					if (!o1.getMedicamento().equalsIgnoreCase(o2.getMedicamento())) {
						return o1.getMedicamento().compareToIgnoreCase(
								o2.getMedicamento());
					}else {
						return 0;
					}
				}
			}
		}
	};

	public List<String> obterDatas(Integer codPaciente, String tipo) {
		List<String> datasMedicamentos = new ArrayList<String>();

		List<VAipPolMdtosAghuHist> medicamentos = this.getVAipPolMdtosAghuHistDAO().obterMedicamentosHist(codPaciente, tipo, null);

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		for (VAipPolMdtosAghuHist medicamento : medicamentos) {
			if (!datasMedicamentos.contains(df.format(medicamento.getDataInicio()))) {
				datasMedicamentos.add(df.format(medicamento.getDataInicio()));
			}
		}
		
		return datasMedicamentos;
	}


	public List<VAipPolMdtosAghuHist> obterMedicamentosHist(Integer codPaciente,
			String tipo, Date dataInicio) {
		List<VAipPolMdtosAghuHist> medicamentos = this.getVAipPolMdtosAghuHistDAO().obterMedicamentosHist(codPaciente, tipo, dataInicio);

		for (VAipPolMdtosAghuHist medicamento : medicamentos) {
			if (medicamento.getMedicamento() == null
					|| medicamento.getMedicamento().isEmpty()) {
				medicamento.setMedicamento(obterMedicamentoHist(
						medicamento.getId().getImePmdAtdSeq(),
						medicamento.getId().getImePmdSeq())
						.getDescricaoFormatadaSemObservacao(false));
			}
		}
		
		Collections.sort(medicamentos, MEDICAMENTOS_POL_COMPARATOR);

		return medicamentos;
	}

	public Long obterMedicamentosHistCount(Integer codPaciente, String tipo) {
		return this.getVAipPolMdtosAghuHistDAO().obterMedicamentosHistCount(codPaciente, tipo);
	}

	

	private MpmPrescricaoMdtosHist obterMedicamentoHist(Integer imePmdAtdSeq, Long imePmdSeq) {
		return this.getPrescricaoMedicaFacade().obterPrescricaoMedicamentoHist(imePmdAtdSeq, imePmdSeq);
	}
		
	/**
	 * ORADB: Procedure MPMC_QTDE_MDTO_POL
	 */
	@SuppressWarnings("ucd")
	public Integer obterQuantidadeMedicamentos(Integer atdSeq,
			String descMedicamento) {

		// Quantidade de milissegundos em um dia
		final Integer tempoDia = 1000 * 60 * 60 * 24;

		Calendar data = Calendar.getInstance();

		AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);

		List<MpmPrescricaoMdtosHist> listaPolMdtosCompleta = this.getPrescricaoMedicaFacade().pesquisaMedicamentosHistPOL(atdSeq);
		List<MpmPrescricaoMdtosHist> listaPolMdtos = new ArrayList<MpmPrescricaoMdtosHist>();

		for (MpmPrescricaoMdtosHist medicamento : listaPolMdtosCompleta) {

			if (descMedicamento.equals(medicamento
					.getDescricaoFormatadaSemObservacao(true))
					&& medicamento.getIndPendente() == DominioIndPendenteItemPrescricao.N) {
				listaPolMdtos.add(medicamento);
			}
		}

		List<Calendar> tabDatas = new ArrayList<Calendar>();
		Float diferencaDias = Float.valueOf("0");

		for (MpmPrescricaoMdtosHist polMdto : listaPolMdtos) {

			Calendar dataInicio = getCalendarSemHora(polMdto.getDthrInicio());
			Calendar dataFim = Calendar.getInstance();

			if (polMdto.getDthrFim() == null) {
				if (atendimento.getDthrFim() != null) {
					dataFim = getCalendarSemHora(atendimento.getDthrFim());
				} else {
					dataFim = getCalendarSemHora(new Date());
				}
			} else {
				dataFim = getCalendarSemHora(polMdto.getDthrFim());
			}

			float diferenca = dataFim.getTimeInMillis()
					- dataInicio.getTimeInMillis();

			diferencaDias = diferenca / tempoDia + 1;

			data = dataInicio;

			for (Integer indice1 = 0; indice1 < diferencaDias; indice1++) {

				if (!tabDatas.contains(data)) {
					Calendar dataAdicionar = Calendar.getInstance();
					dataAdicionar.set(data.get(Calendar.YEAR), data
							.get(Calendar.MONTH), data
							.get(Calendar.DAY_OF_MONTH), 0, 0, 0);// = data;
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

		data.set(data.get(Calendar.YEAR), data.get(Calendar.MONTH), data
				.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

		data.set(Calendar.MILLISECOND, 0);

		return data;
	}
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected VAipPolMdtosAghuHistDAO getVAipPolMdtosAghuHistDAO() {
		return vAipPolMdtosAghuHistDAO;
	}
}
