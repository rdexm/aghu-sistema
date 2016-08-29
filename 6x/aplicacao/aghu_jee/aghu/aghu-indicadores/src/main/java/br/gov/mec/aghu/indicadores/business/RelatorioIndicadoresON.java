package br.gov.mec.aghu.indicadores.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.indicadores.vo.IndHospClinicaEspVO;
import br.gov.mec.aghu.indicadores.vo.IndHospClinicaEspViewVO;
import br.gov.mec.aghu.indicadores.vo.UnidadeIndicadoresVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinIndicadoresHospitalares;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON para Relatório de Indicadores.
 * 
 * @author evchneider
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class RelatorioIndicadoresON extends BaseBusiness {

private static final String TOT = "TOT";

private static final Log LOG = LogFactory.getLog(RelatorioIndicadoresON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

	private static final long serialVersionUID = -3627768852199908876L;

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	public List<IndHospClinicaEspVO> gerarRelatorioClinicaEspecialidade(Date mes) {
		List<IndHospClinicaEspViewVO> indicadoresClinicaViewVoList = new ArrayList<IndHospClinicaEspViewVO>();

		indicadoresClinicaViewVoList.addAll(this.pesquisarQuery1IndicadoresClinica(mes));
		indicadoresClinicaViewVoList.addAll(this.pesquisarQuery2IndicadoresClinica(mes));
		indicadoresClinicaViewVoList.addAll(this.pesquisarQuery3IndicadoresClinica(mes));
		indicadoresClinicaViewVoList.addAll(this.pesquisarQuery4IndicadoresClinica(mes));
		indicadoresClinicaViewVoList.addAll(this.pesquisarQuery5IndicadoresClinica(mes));

		List<IndHospClinicaEspVO> indicadoresClinicaVoList = transformarIndicadoresClinicaViewParaReport(indicadoresClinicaViewVoList);

		return indicadoresClinicaVoList;
	}

	private List<IndHospClinicaEspVO> transformarIndicadoresClinicaViewParaReport(
			List<IndHospClinicaEspViewVO> indicadoresClinicaViewVoList) {

		List<IndHospClinicaEspVO> indicadoresClinicaVoList = new ArrayList<IndHospClinicaEspVO>();

		for (IndHospClinicaEspViewVO view : indicadoresClinicaViewVoList) {
			IndHospClinicaEspVO report = new IndHospClinicaEspVO();
			report.setTipo(Long.valueOf(1));
			report.setCompetencia(view.getCompetencia());
			report.setPacientesMesAnteriror(view.getPacientesMesAnterior());
			report.setTotAltas(view.getTotAltas());
			report.setTotEntrInternacoes(view.getTotInternacoesMes());
			report.setTotEntrOutrasEspecialidades(Long.valueOf(0));
			report.setTotEntrOutrasUnidades(view.getTotEntrOutrasClinicas());
			report.setTotInternacoesMes(view.getTotEntradas());
			report.setTotIntAreaSatelite(view.getTotIntAreaSatelite());
			report.setTotObitosMais24h(view.getTotObitosMais48h());
			report.setTotObitosMenos24h(view.getTotObitosMenos48h());
			report.setTotSaidas(view.getTotSaidas());
			report.setTotSaidOutrasEspecialidades(Long.valueOf(0));
			report.setTotSaidOutrasUnidades(view.getTotSaidOutrasClinicas());
			report.setTotSaldo(view.getTotSaldo());
			report.setCapacReferencial(view.getCapacidade());
			report.setTotBloqueios(view.getTotBloqueios());
			report.setPacHospitalDia(view.getPacienteDia());
			report.setMediaPacienteDia(view.getMediaPacienteDia());
			report.setLeitoDia(view.getLeitoDia());
			report.setPercentualOcupacao(view.getPercentualOcupacao());
			report.setMediaPermanencia(view.getMediaPermanencia());
			report.setIndiceMortGeral(view.getIndiceMortGeral());
			report.setIndiceMortEspecialidade(view.getIndiceMortEspecifico());
			report.setIndiceIntervaloSubstituicao(view.getIndiceIntervaloSubstituicao());
			report.setIndiceRenovacao(view.getIndiceRenovacao());
			if (view.getDescricao() != null) {
				report.setSigla(view.getDescricao().substring(0, 3));
			}
			report.setClcCodigo(view.getClcCodigo());
			report.setTipoUnidade(view.getTipoUnidade());

			indicadoresClinicaVoList.add(report);
		}

		return indicadoresClinicaVoList;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private List<IndHospClinicaEspViewVO> pesquisarQuery5IndicadoresClinica(Date mes) {

		List<AinIndicadoresHospitalares> lista = getInternacaoFacade()
				.pesquisarQuery5IndicadoresClinica(mes);

		IndHospClinicaEspViewVO totaisCalculo = new IndHospClinicaEspViewVO("zeros");

		for (AinIndicadoresHospitalares indicadores : lista) {

			totaisCalculo.setCompetencia(indicadores.getCompetenciaInternacao());
			totaisCalculo.setTotInternacoesMes(totaisCalculo.getTotInternacoesMes()
					+ indicadores.getTotalInternacoesMes());
			totaisCalculo.setTotIntAreaSatelite(totaisCalculo.getTotIntAreaSatelite()
					+ indicadores.getTotalIntAreaSatelite());
			totaisCalculo.setTotEntradas(totaisCalculo.getTotEntradas()
					+ indicadores.getTotalEntradas());
			totaisCalculo.setTotAltas(totaisCalculo.getTotAltas() + indicadores.getTotalAltas());
			totaisCalculo.setTotObitosMais48h(totaisCalculo.getTotObitosMais48h()
					+ indicadores.getTotalObitosMais48h());
			totaisCalculo.setTotObitosMenos48h(totaisCalculo.getTotObitosMenos48h()
					+ (indicadores.getTotalObitosMenos48h()));
			totaisCalculo.setTotSaidOutrasClinicas(totaisCalculo.getTotSaidOutrasClinicas()
					+ (indicadores.getTotalSaidaOutrasClinicas()));
			totaisCalculo.setTotSaidas(totaisCalculo.getTotSaidas() + indicadores.getTotalSaidas());
			totaisCalculo.setPacientesMesAnterior(totaisCalculo.getPacientesMesAnterior()
					+ indicadores.getPacientesMesAnterior());
			totaisCalculo.setTotSaldo(totaisCalculo.getTotSaldo() + (indicadores.getTotalSaldo()));
			totaisCalculo.setCapacidade(totaisCalculo.getCapacidade()
					+ indicadores.getCapacidadeReferencial().longValue());
			totaisCalculo.setTotBloqueios(totaisCalculo.getTotBloqueios().add(
					indicadores.getTotalBloqueios()));
			totaisCalculo.setPacienteDia(totaisCalculo.getPacienteDia()
					+ (indicadores.getPacienteDia().doubleValue()));
			totaisCalculo.setMediaPacienteDia(totaisCalculo.getMediaPacienteDia()
					+ (indicadores.getMediaPacienteDia().doubleValue()));
			totaisCalculo.setLeitoDia(totaisCalculo.getLeitoDia() + (indicadores.getLeitoDia()));
			totaisCalculo.setPercentualOcupacao(totaisCalculo.getPercentualOcupacao()
					+ (indicadores.getPercentualOcupacao().doubleValue()));
			totaisCalculo.setMediaPermanencia(totaisCalculo.getMediaPermanencia()
					+ (indicadores.getMediaPermanencia().doubleValue()));
			totaisCalculo.setIndiceMortGeral(totaisCalculo.getIndiceMortGeral()
					+ (indicadores.getIndiceMorteGeral().doubleValue()));
			totaisCalculo.setIndiceMortEspecifico(totaisCalculo.getIndiceMortEspecifico()
					+ (indicadores.getIndiceMorteEspecifico().doubleValue()));
			totaisCalculo.setIndiceIntervaloSubstituicao(totaisCalculo
					.getIndiceIntervaloSubstituicao()
					+ (indicadores.getIndiceIntervaloSubstituicao().doubleValue()));
			totaisCalculo.setIndiceRenovacao(totaisCalculo.getIndiceRenovacao()
					+ (indicadores.getIndiceRenovacao().doubleValue()));
			totaisCalculo.setTotSaidOutrasUnidades(totaisCalculo.getTotSaidOutrasUnidades()
					+ (indicadores.getTotalSaidaOutrasUnidades()));

		}

		IndHospClinicaEspViewVO total = new IndHospClinicaEspViewVO();
		total.setCompetencia(totaisCalculo.getCompetencia());
		total.setTipoUnidade(Long.valueOf(3));
		total.setClcCodigo(Long.valueOf(9));
		total.setClinica(TOT);
		total.setDescricao(TOT);
		total.setTotInternacoesMes(totaisCalculo.getTotInternacoesMes());
		total.setTotIntAreaSatelite(totaisCalculo.getTotIntAreaSatelite());
		total.setTotEntrOutrasClinicas(Long.valueOf(0));
		total.setTotEntradas(calcularTotalEntradas(totaisCalculo));
		total.setTotAltas(totaisCalculo.getTotAltas());
		total.setTotObitosMenos48h(totaisCalculo.getTotObitosMenos48h());
		total.setTotObitosMais48h(totaisCalculo.getTotObitosMais48h());
		total.setTotSaidOutrasClinicas(Long.valueOf(0));
		total.setTotSaidas(calcularTotalSaidas(totaisCalculo));
		total.setPacientesMesAnterior(calcularPacientesMesAnterior2(totaisCalculo));
		total.setTotSaldo(totaisCalculo.getTotSaldo());
		total.setCapacidade(totaisCalculo.getCapacidade());
		total.setTotBloqueios(calcularTotalBloqueios(totaisCalculo.getCompetencia(), totaisCalculo
				.getTotBloqueios().doubleValue()));
		total.setPacienteDia(totaisCalculo.getPacienteDia());
		total.setMediaPacienteDia(calcularMediaPacienteDia(totaisCalculo));
		total.setLeitoDia(totaisCalculo.getLeitoDia());
		total.setPercentualOcupacao(calcularPercentualOcupacao(totaisCalculo.getLeitoDia(),
				totaisCalculo.getPacienteDia()));
		total.setMediaPermanencia(calcularMediaPermanencia(totaisCalculo.getTotAltas(),
				totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h(),
				totaisCalculo.getPacienteDia()));
		total.setIndiceMortGeral(calcularIndiceMortalidadeGeral(totaisCalculo.getTotAltas(),
				totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h()));
		total.setIndiceMortEspecifico(calcularIndiceMortalidadeEspecialidade(
				totaisCalculo.getTotAltas(), totaisCalculo.getTotObitosMais48h(),
				totaisCalculo.getTotObitosMenos48h()));
		total.setIndiceIntervaloSubstituicao(calcularIndiceIntervaloSubstituicao(
				totaisCalculo.getTotAltas(), totaisCalculo.getTotObitosMais48h(),
				totaisCalculo.getTotObitosMenos48h(), totaisCalculo.getPacienteDia(),
				totaisCalculo.getLeitoDia(), total.getMediaPermanencia(),
				total.getPercentualOcupacao()));
		total.setIndiceRenovacao(calcularIndiceRenovacao(totaisCalculo.getTotAltas(),
				totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h(),
				new Double(totaisCalculo.getCapacidade())));

		List<IndHospClinicaEspViewVO> retorno = new ArrayList<IndHospClinicaEspViewVO>();
		retorno.add(total);

		return retorno;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private List<IndHospClinicaEspViewVO> pesquisarQuery4IndicadoresClinica(Date mes) {

		List<AinIndicadoresHospitalares> lista = getInternacaoFacade()
				.pesquisarQuery4IndicadoresClinica(mes);

		IndHospClinicaEspViewVO totaisCalculo = new IndHospClinicaEspViewVO("zeros");

		for (AinIndicadoresHospitalares indicadores : lista) {

			totaisCalculo.setCompetencia(indicadores.getCompetenciaInternacao());
			totaisCalculo.setTotInternacoesMes(totaisCalculo.getTotInternacoesMes()
					+ indicadores.getTotalInternacoesMes());
			totaisCalculo.setTotIntAreaSatelite(totaisCalculo.getTotIntAreaSatelite()
					+ indicadores.getTotalIntAreaSatelite());
			totaisCalculo.setTotEntradas(totaisCalculo.getTotEntradas()
					+ indicadores.getTotalEntradas());
			totaisCalculo.setTotAltas(totaisCalculo.getTotAltas() + indicadores.getTotalAltas());
			totaisCalculo.setTotObitosMais48h(totaisCalculo.getTotObitosMais48h()
					+ indicadores.getTotalObitosMais48h());
			totaisCalculo.setTotObitosMenos48h(totaisCalculo.getTotObitosMenos48h()
					+ (indicadores.getTotalObitosMenos48h()));
			totaisCalculo.setTotSaidOutrasClinicas(totaisCalculo.getTotSaidOutrasClinicas()
					+ (indicadores.getTotalSaidaOutrasClinicas()));
			totaisCalculo.setTotSaidas(totaisCalculo.getTotSaidas() + indicadores.getTotalSaidas());
			totaisCalculo.setPacientesMesAnterior(totaisCalculo.getPacientesMesAnterior()
					+ indicadores.getPacientesMesAnterior());
			totaisCalculo.setTotSaldo(totaisCalculo.getTotSaldo() + (indicadores.getTotalSaldo()));
			totaisCalculo.setCapacidade(totaisCalculo.getCapacidade()
					+ indicadores.getCapacidadeReferencial().longValue());
			totaisCalculo.setTotBloqueios(totaisCalculo.getTotBloqueios().add(
					indicadores.getTotalBloqueios()));
			totaisCalculo.setPacienteDia(totaisCalculo.getPacienteDia()
					+ (indicadores.getPacienteDia().doubleValue()));
			totaisCalculo.setMediaPacienteDia(totaisCalculo.getMediaPacienteDia()
					+ (indicadores.getMediaPacienteDia().doubleValue()));
			totaisCalculo.setLeitoDia(totaisCalculo.getLeitoDia() + (indicadores.getLeitoDia()));
			totaisCalculo.setPercentualOcupacao(totaisCalculo.getPercentualOcupacao()
					+ (indicadores.getPercentualOcupacao().doubleValue()));
			totaisCalculo.setMediaPermanencia(totaisCalculo.getMediaPermanencia()
					+ (indicadores.getMediaPermanencia().doubleValue()));
			totaisCalculo.setIndiceMortGeral(totaisCalculo.getIndiceMortGeral()
					+ (indicadores.getIndiceMorteGeral().doubleValue()));
			totaisCalculo.setIndiceMortEspecifico(totaisCalculo.getIndiceMortEspecifico()
					+ (indicadores.getIndiceMorteEspecifico().doubleValue()));
			totaisCalculo.setIndiceIntervaloSubstituicao(totaisCalculo
					.getIndiceIntervaloSubstituicao()
					+ (indicadores.getIndiceIntervaloSubstituicao().doubleValue()));
			totaisCalculo.setIndiceRenovacao(totaisCalculo.getIndiceRenovacao()
					+ (indicadores.getIndiceRenovacao().doubleValue()));
			totaisCalculo.setTotSaidOutrasUnidades(totaisCalculo.getTotSaidOutrasUnidades()
					+ (indicadores.getTotalSaidaOutrasUnidades()));

		}

		IndHospClinicaEspViewVO total = new IndHospClinicaEspViewVO();
		if (lista != null && !lista.isEmpty()) {
			total.setCompetencia(totaisCalculo.getCompetencia());
			total.setTipoUnidade(Long.valueOf(2));
			total.setClcCodigo(Long.valueOf(9));
			total.setClinica(TOT);
			total.setDescricao(TOT);
			total.setTotInternacoesMes(totaisCalculo.getTotInternacoesMes());
			total.setTotIntAreaSatelite(totaisCalculo.getTotIntAreaSatelite());
			total.setTotEntrOutrasClinicas(Long.valueOf(0));
			total.setTotEntradas(calcularTotalEntradas(totaisCalculo));
			total.setTotAltas(totaisCalculo.getTotAltas());
			total.setTotObitosMenos48h(totaisCalculo.getTotObitosMenos48h());
			total.setTotObitosMais48h(totaisCalculo.getTotObitosMais48h());
			total.setTotSaidOutrasClinicas(totaisCalculo.getTotSaidOutrasClinicas());
			total.setTotSaidas(calcularTotalSaidas4(totaisCalculo));
			total.setPacientesMesAnterior(calcularPacientesMesAnterior4(totaisCalculo));
			total.setTotSaldo(totaisCalculo.getTotSaldo());
			total.setCapacidade(totaisCalculo.getCapacidade());
			total.setTotBloqueios(calcularTotalBloqueios(totaisCalculo.getCompetencia(),
					totaisCalculo.getTotBloqueios().doubleValue()));
			total.setPacienteDia(totaisCalculo.getPacienteDia());
			total.setMediaPacienteDia(calcularMediaPacienteDia(totaisCalculo));
			total.setLeitoDia(totaisCalculo.getLeitoDia());
			total.setPercentualOcupacao(calcularPercentualOcupacao(totaisCalculo.getLeitoDia(),
					totaisCalculo.getPacienteDia()));
			total.setMediaPermanencia(calcularMediaPermanencia(totaisCalculo.getTotAltas(),
					totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h(),
					totaisCalculo.getPacienteDia()));
			total.setIndiceMortGeral(calcularIndiceMortalidadeGeral(totaisCalculo.getTotAltas(),
					totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h()));
			total.setIndiceMortEspecifico(calcularIndiceMortalidadeEspecialidade(
					totaisCalculo.getTotAltas(), totaisCalculo.getTotObitosMais48h(),
					totaisCalculo.getTotObitosMenos48h()));
			total.setIndiceIntervaloSubstituicao(calcularIndiceIntervaloSubstituicao(
					totaisCalculo.getTotAltas(), totaisCalculo.getTotObitosMais48h(),
					totaisCalculo.getTotObitosMenos48h(), totaisCalculo.getPacienteDia(),
					totaisCalculo.getLeitoDia(), total.getMediaPermanencia(),
					total.getPercentualOcupacao()));
			total.setIndiceRenovacao(calcularIndiceRenovacao(totaisCalculo.getTotAltas(),
					totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h(),
					new Double(totaisCalculo.getCapacidade())));

			List<IndHospClinicaEspViewVO> retorno = new ArrayList<IndHospClinicaEspViewVO>();
			retorno.add(total);

			return retorno;
		} else {
			// Caso não tenha econtrado nenhum registro, retorna uma lista em
			// branco
			return new ArrayList<IndHospClinicaEspViewVO>();
		}

	}

	private Long calcularPacientesMesAnterior4(IndHospClinicaEspViewVO totaisCalculo) {
		// tot_saldo+tot_altas + tot_obitos_menos_48h + tot_obitos_mais_48h +
		// tot_said_outras_clinicas + tot_said_outras_unidades
		// - (tot_internacoes_mes + tot_int_area_satelite +
		// tot_entr_outras_unidades)
		return Long.valueOf(
				totaisCalculo.getTotSaldo()
						+ totaisCalculo.getTotAltas()
						+ totaisCalculo.getTotObitosMenos48h()
						+ totaisCalculo.getTotObitosMais48h()
						+ totaisCalculo.getTotSaidOutrasClinicas()
						+ totaisCalculo.getTotSaidOutrasUnidades()
						- (totaisCalculo.getTotInternacoesMes()
								+ totaisCalculo.getTotIntAreaSatelite() + totaisCalculo
								.getTotSaidOutrasUnidades()));
	}

	private Long calcularTotalSaidas4(IndHospClinicaEspViewVO totaisCalculo) {
		// tot_altas+tot_obitos_menos_48h+tot_obitos_mais_48h +
		// tot_said_outras_clinicas
		return Long.valueOf(totaisCalculo.getTotAltas() + totaisCalculo.getTotObitosMenos48h()
				+ totaisCalculo.getTotObitosMais48h())
				+ totaisCalculo.getTotSaidOutrasClinicas();
	}

	private List<IndHospClinicaEspViewVO> pesquisarQuery3IndicadoresClinica(Date mes) {

		List<AinIndicadoresHospitalares> lista = getInternacaoFacade()
				.pesquisarQuery3IndicadoresClinica(mes);

		List<IndHospClinicaEspViewVO> listIndicadoresClinica = new ArrayList<IndHospClinicaEspViewVO>();
		for (AinIndicadoresHospitalares indicadores : lista) {

			IndHospClinicaEspViewVO indicadorClinicaViewVo = new IndHospClinicaEspViewVO();

			indicadorClinicaViewVo.setCompetencia(indicadores.getCompetenciaInternacao());
			indicadorClinicaViewVo.setTipoUnidade(Long.valueOf(2));
			indicadorClinicaViewVo.setClcCodigo(Long.valueOf(indicadores.getClinica().getCodigo()));
			indicadorClinicaViewVo.setClinica(indicadores.getClinica().getDescricao()
					.substring(0, 3));
			indicadorClinicaViewVo.setTotInternacoesMes(Long.valueOf(indicadores
					.getTotalInternacoesMes()));
			indicadorClinicaViewVo.setTotIntAreaSatelite(Long.valueOf(indicadores
					.getTotalIntAreaSatelite()));
			indicadorClinicaViewVo.setTotEntrOutrasClinicas(Long.valueOf(indicadores
					.getTotalEntreOutrasClinicas()));
			indicadorClinicaViewVo.setTotEntradas(Long.valueOf(indicadores.getTotalEntradas()));
			indicadorClinicaViewVo.setTotAltas(Long.valueOf(indicadores.getTotalAltas()));
			indicadorClinicaViewVo
					.setTotObitosMais48h(Long.valueOf(indicadores.getTotalObitosMais48h()));
			indicadorClinicaViewVo.setTotObitosMenos48h(Long.valueOf(indicadores
					.getTotalObitosMenos48h()));
			indicadorClinicaViewVo.setTotSaidOutrasClinicas(Long.valueOf(indicadores
					.getTotalSaidaOutrasClinicas()));
			indicadorClinicaViewVo.setTotSaidas(Long.valueOf(indicadores.getTotalSaidas()));
			indicadorClinicaViewVo
					.setPacientesMesAnterior(calcularPacientesMesAnterior3(indicadores));
			indicadorClinicaViewVo.setTotSaldo(Long.valueOf(indicadores.getTotalSaldo()));
			indicadorClinicaViewVo.setCapacidade(indicadores.getCapacidadeReferencial()
					.doubleValue());
			indicadorClinicaViewVo.setTotBloqueios(calcularTotalBloqueios(indicadores
					.getCompetenciaInternacao(), indicadores.getTotalBloqueios().doubleValue()));
			indicadorClinicaViewVo.setPacienteDia(indicadores.getPacienteDia().doubleValue());
			indicadorClinicaViewVo.setMediaPacienteDia(indicadores.getMediaPacienteDia()
					.doubleValue());
			indicadorClinicaViewVo.setLeitoDia(Long.valueOf(indicadores.getLeitoDia()));
			indicadorClinicaViewVo.setPercentualOcupacao(indicadores.getPercentualOcupacao()
					.doubleValue());
			indicadorClinicaViewVo.setMediaPermanencia(indicadores.getMediaPermanencia()
					.doubleValue());
			indicadorClinicaViewVo.setIndiceMortGeral(indicadores.getIndiceMorteGeral()
					.doubleValue());
			indicadorClinicaViewVo.setIndiceMortEspecifico(indicadores.getIndiceMorteEspecifico()
					.doubleValue());
			indicadorClinicaViewVo.setIndiceIntervaloSubstituicao(indicadores
					.getIndiceIntervaloSubstituicao().doubleValue());
			indicadorClinicaViewVo.setIndiceRenovacao(indicadores.getIndiceRenovacao()
					.doubleValue());

			indicadorClinicaViewVo.setDescricao(indicadores.getClinica().getDescricao());

			listIndicadoresClinica.add(indicadorClinicaViewVo);

		}

		return listIndicadoresClinica;
	}

	private List<IndHospClinicaEspViewVO> pesquisarQuery2IndicadoresClinica(Date mes) {

		List<AinIndicadoresHospitalares> lista = getInternacaoFacade()
				.pesquisarQuery2IndicadoresClinica(mes);

		IndHospClinicaEspViewVO totaisCalculo = new IndHospClinicaEspViewVO("zeros");

		for (AinIndicadoresHospitalares indicadores : lista) {

			totaisCalculo.setCompetencia(indicadores.getCompetenciaInternacao());
			totaisCalculo.setTotInternacoesMes(totaisCalculo.getTotInternacoesMes()
					+ indicadores.getTotalInternacoesMes());
			totaisCalculo.setTotIntAreaSatelite(totaisCalculo.getTotIntAreaSatelite()
					+ indicadores.getTotalIntAreaSatelite());
			totaisCalculo.setTotEntradas(totaisCalculo.getTotEntradas()
					+ indicadores.getTotalEntradas());
			totaisCalculo.setTotAltas(totaisCalculo.getTotAltas() + indicadores.getTotalAltas());
			totaisCalculo.setTotObitosMais48h(totaisCalculo.getTotObitosMais48h()
					+ indicadores.getTotalObitosMais48h());
			totaisCalculo.setTotObitosMenos48h(totaisCalculo.getTotObitosMenos48h()
					+ (indicadores.getTotalObitosMenos48h()));
			totaisCalculo.setTotSaidOutrasClinicas(totaisCalculo.getTotSaidOutrasClinicas()
					+ (indicadores.getTotalSaidaOutrasClinicas()));
			totaisCalculo.setTotSaidas(totaisCalculo.getTotSaidas() + indicadores.getTotalSaidas());
			totaisCalculo.setPacientesMesAnterior(totaisCalculo.getPacientesMesAnterior()
					+ indicadores.getPacientesMesAnterior());
			totaisCalculo.setTotSaldo(totaisCalculo.getTotSaldo() + (indicadores.getTotalSaldo()));
			totaisCalculo.setCapacidade(totaisCalculo.getCapacidade()
					+ indicadores.getCapacidadeReferencial().longValue());
			totaisCalculo.setTotBloqueios(totaisCalculo.getTotBloqueios().add(
					indicadores.getTotalBloqueios()));
			totaisCalculo.setPacienteDia(totaisCalculo.getPacienteDia()
					+ (indicadores.getPacienteDia().doubleValue()));
			totaisCalculo.setMediaPacienteDia(totaisCalculo.getMediaPacienteDia()
					+ (indicadores.getMediaPacienteDia().doubleValue()));
			totaisCalculo.setLeitoDia(totaisCalculo.getLeitoDia() + (indicadores.getLeitoDia()));
			totaisCalculo.setPercentualOcupacao(totaisCalculo.getPercentualOcupacao()
					+ (indicadores.getPercentualOcupacao().doubleValue()));
			totaisCalculo.setMediaPermanencia(totaisCalculo.getMediaPermanencia()
					+ (indicadores.getMediaPermanencia().doubleValue()));
			totaisCalculo.setIndiceMortGeral(totaisCalculo.getIndiceMortGeral()
					+ (indicadores.getIndiceMorteGeral().doubleValue()));
			totaisCalculo.setIndiceMortEspecifico(totaisCalculo.getIndiceMortEspecifico()
					+ (indicadores.getIndiceMorteEspecifico().doubleValue()));
			totaisCalculo.setIndiceIntervaloSubstituicao(totaisCalculo
					.getIndiceIntervaloSubstituicao()
					+ (indicadores.getIndiceIntervaloSubstituicao().doubleValue()));
			totaisCalculo.setIndiceRenovacao(totaisCalculo.getIndiceRenovacao()
					+ (indicadores.getIndiceRenovacao().doubleValue()));

		}

		IndHospClinicaEspViewVO total = new IndHospClinicaEspViewVO();
		total.setCompetencia(totaisCalculo.getCompetencia());
		total.setTipoUnidade(Long.valueOf(1));
		total.setClcCodigo(Long.valueOf(9));
		total.setClinica(TOT);
		total.setDescricao(TOT);
		total.setTotInternacoesMes(totaisCalculo.getTotInternacoesMes());
		total.setTotIntAreaSatelite(totaisCalculo.getTotIntAreaSatelite());
		total.setTotEntrOutrasClinicas(Long.valueOf(0));
		total.setTotEntradas(calcularTotalEntradas(totaisCalculo));
		total.setTotAltas(totaisCalculo.getTotAltas());
		total.setTotObitosMenos48h(totaisCalculo.getTotObitosMenos48h());
		total.setTotObitosMais48h(totaisCalculo.getTotObitosMais48h());
		total.setTotSaidOutrasClinicas(Long.valueOf(0));
		total.setTotSaidas(calcularTotalSaidas(totaisCalculo));
		total.setPacientesMesAnterior(calcularPacientesMesAnterior2(totaisCalculo));
		total.setTotSaldo(totaisCalculo.getTotSaldo());
		total.setCapacidade(totaisCalculo.getCapacidade());
		total.setTotBloqueios(calcularTotalBloqueios(totaisCalculo.getCompetencia(), totaisCalculo
				.getTotBloqueios().doubleValue()));
		total.setPacienteDia(totaisCalculo.getPacienteDia());
		total.setMediaPacienteDia(calcularMediaPacienteDia(totaisCalculo));
		total.setLeitoDia(totaisCalculo.getLeitoDia());
		total.setPercentualOcupacao(calcularPercentualOcupacao(totaisCalculo.getLeitoDia(),
				totaisCalculo.getPacienteDia()));
		total.setMediaPermanencia(calcularMediaPermanencia(totaisCalculo.getTotAltas(),
				totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h(),
				totaisCalculo.getPacienteDia()));
		total.setIndiceMortGeral(calcularIndiceMortalidadeGeral(totaisCalculo.getTotAltas(),
				totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h()));
		total.setIndiceMortEspecifico(calcularIndiceMortalidadeEspecialidade(
				totaisCalculo.getTotAltas(), totaisCalculo.getTotObitosMais48h(),
				totaisCalculo.getTotObitosMenos48h()));
		total.setIndiceIntervaloSubstituicao(calcularIndiceIntervaloSubstituicao(
				totaisCalculo.getTotAltas(), totaisCalculo.getTotObitosMais48h(),
				totaisCalculo.getTotObitosMenos48h(), totaisCalculo.getPacienteDia(),
				totaisCalculo.getLeitoDia(), total.getMediaPermanencia(),
				total.getPercentualOcupacao()));
		total.setIndiceRenovacao(calcularIndiceRenovacao(totaisCalculo.getTotAltas(),
				totaisCalculo.getTotObitosMais48h(), totaisCalculo.getTotObitosMenos48h(),
				new Double(totaisCalculo.getCapacidade())));

		List<IndHospClinicaEspViewVO> retorno = new ArrayList<IndHospClinicaEspViewVO>();
		retorno.add(total);

		return retorno;
	}

	private Double calcularMediaPacienteDia(IndHospClinicaEspViewVO totaisCalculo) {
		// round(sum(paciente_dia)/to_number(to_char(last_day(competencia_internacao),'dd')),2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(totaisCalculo.getCompetencia());
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);

		return totaisCalculo.getPacienteDia().doubleValue() / lastDate;
	}

	private Long calcularPacientesMesAnterior3(AinIndicadoresHospitalares indicadores) {
		// tot_saldo+tot_saidas+
		// tot_said_outras_unidades-tot_entradas-tot_entr_outras_unidades

		return Long.valueOf(
				(indicadores.getTotalSaldo() + indicadores.getTotalSaidas() + indicadores
						.getTotalSaidaOutrasUnidades())
						- indicadores.getTotalEntradas()
						- indicadores.getTotalEntreOutrasUnidades());
	}

	private Long calcularPacientesMesAnterior2(IndHospClinicaEspViewVO totaisCalculo) {
		// tot_saldo+tot_altas + tot_obitos_menos_48h + tot_obitos_mais_48h -
		// (tot_internacoes_mes + tot_int_area_satelite
		return Long.valueOf(totaisCalculo.getTotSaldo() + totaisCalculo.getTotAltas()
				+ totaisCalculo.getTotObitosMenos48h() + totaisCalculo.getTotObitosMais48h()
				- (totaisCalculo.getTotInternacoesMes() + totaisCalculo.getTotIntAreaSatelite()));
	}

	private Long calcularTotalSaidas(IndHospClinicaEspViewVO totaisCalculo) {
		// tot_altas+tot_obitos_menos_48h+tot_obitos_mais_48h
		return Long.valueOf(totaisCalculo.getTotAltas() + totaisCalculo.getTotObitosMenos48h()
				+ totaisCalculo.getTotObitosMais48h());
	}

	private Long calcularTotalEntradas(IndHospClinicaEspViewVO totaisCalculo) {
		// tot_internacoes_mes+tot_int_area_satelite
		return Long.valueOf(totaisCalculo.getTotInternacoesMes()
				+ totaisCalculo.getTotIntAreaSatelite());
	}

	private List<IndHospClinicaEspViewVO> pesquisarQuery1IndicadoresClinica(Date mes) {

		List<AinIndicadoresHospitalares> lista = getInternacaoFacade()
				.pesquisarQuery1IndicadoresClinica(mes);

		List<IndHospClinicaEspViewVO> listIndicadoresClinica = new ArrayList<IndHospClinicaEspViewVO>();
		for (AinIndicadoresHospitalares indicadores : lista) {

			IndHospClinicaEspViewVO indicadorClinicaViewVo = new IndHospClinicaEspViewVO();

			indicadorClinicaViewVo.setCompetencia(indicadores.getCompetenciaInternacao());

			indicadorClinicaViewVo.setClcCodigo(Long.valueOf(indicadores.getClinica().getCodigo()));
			indicadorClinicaViewVo.setClinica(indicadores.getClinica().getDescricao()
					.substring(0, 3));
			indicadorClinicaViewVo.setTotInternacoesMes(Long.valueOf(indicadores
					.getTotalInternacoesMes()));
			indicadorClinicaViewVo.setTotIntAreaSatelite(Long.valueOf(indicadores
					.getTotalIntAreaSatelite()));
			indicadorClinicaViewVo.setTotEntrOutrasClinicas(Long.valueOf(indicadores
					.getTotalEntreOutrasClinicas()));
			indicadorClinicaViewVo.setTotEntradas(Long.valueOf(indicadores.getTotalEntradas()));
			indicadorClinicaViewVo.setTotAltas(Long.valueOf(indicadores.getTotalAltas()));
			indicadorClinicaViewVo
					.setTotObitosMais48h(Long.valueOf(indicadores.getTotalObitosMais48h()));
			indicadorClinicaViewVo.setTotObitosMenos48h(Long.valueOf(indicadores
					.getTotalObitosMenos48h()));
			indicadorClinicaViewVo.setTotSaidOutrasClinicas(Long.valueOf(indicadores
					.getTotalSaidaOutrasClinicas()));
			indicadorClinicaViewVo.setTotSaidas(Long.valueOf(indicadores.getTotalSaidas()));
			indicadorClinicaViewVo.setTotSaldo(Long.valueOf(indicadores.getTotalSaldo()));
			indicadorClinicaViewVo.setCapacidade(indicadores.getCapacidadeReferencial()
					.doubleValue());
			indicadorClinicaViewVo.setPacienteDia(indicadores.getPacienteDia().doubleValue());
			indicadorClinicaViewVo.setMediaPacienteDia(indicadores.getMediaPacienteDia()
					.doubleValue());
			indicadorClinicaViewVo.setLeitoDia(Long.valueOf(indicadores.getLeitoDia()));
			indicadorClinicaViewVo.setPercentualOcupacao(indicadores.getPercentualOcupacao()
					.doubleValue());
			indicadorClinicaViewVo.setMediaPermanencia(indicadores.getMediaPermanencia()
					.doubleValue());
			indicadorClinicaViewVo.setIndiceMortGeral(indicadores.getIndiceMorteGeral()
					.doubleValue());
			indicadorClinicaViewVo.setIndiceMortEspecifico(indicadores.getIndiceMorteEspecifico()
					.doubleValue());
			indicadorClinicaViewVo.setIndiceIntervaloSubstituicao(indicadores
					.getIndiceIntervaloSubstituicao().doubleValue());
			indicadorClinicaViewVo.setIndiceRenovacao(indicadores.getIndiceRenovacao()
					.doubleValue());
			indicadorClinicaViewVo.setTipoUnidade(Long.valueOf(1));
			indicadorClinicaViewVo.setDescricao(indicadores.getClinica().getDescricao());
			indicadorClinicaViewVo
					.setPacientesMesAnterior(calcularPacientesMesAnterior(indicadores));
			indicadorClinicaViewVo.setTotBloqueios(calcularTotalBloqueios(indicadores
					.getCompetenciaInternacao(), indicadores.getTotalBloqueios().doubleValue()));

			listIndicadoresClinica.add(indicadorClinicaViewVo);

		}

		return listIndicadoresClinica;
	}

	private BigDecimal calcularTotalBloqueios(Date competencia, Double totalBloqueios) {
		// round(tot_bloqueios/to_number(to_char(last_day(competencia_internacao),'dd')),2)

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(competencia);
		int lastDate = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE, lastDate);

		return BigDecimal.valueOf(totalBloqueios / lastDate);
	}

	private Long calcularPacientesMesAnterior(AinIndicadoresHospitalares indicadores) {
		// tot_saldo+tot_saidas-tot_entradas

		return Long.valueOf((indicadores.getTotalSaldo() + indicadores.getTotalSaidas())
				- indicadores.getTotalEntradas());
	}

	/**
	 * Método que faz pesquisa em AinIndicadoresHospitalares e depois itera o
	 * resultSet para formatar os valores, colocando os mesmos dentro de uma
	 * lista (List<UnidadeIndicadoresVO>). Esse método retorna os indicadores
	 * gerais por unidade.
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	private List<UnidadeIndicadoresVO> formatarValoresGeraisIndicadoresUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia) {
		UnidadeIndicadoresVO vo = null;
		List<UnidadeIndicadoresVO> retorno = new ArrayList<UnidadeIndicadoresVO>();

		List<AinIndicadoresHospitalares> indicadoresUnidade = this
				.getInternacaoFacade().pesquisarIndicadoresGeraisUnidade(tipoUnidade,
						mesCompetencia);

		for (AinIndicadoresHospitalares indicadorHospitalar : indicadoresUnidade) {

			AghUnidadesFuncionais unidadeFuncional = indicadorHospitalar.getUnidadeFuncional();

			vo = new UnidadeIndicadoresVO();
			vo.setCompetenciaInternacao(indicadorHospitalar.getCompetenciaInternacao());
			vo.setTipo(Long.valueOf(1));
			vo.setAndarAlaDescricao(unidadeFuncional.getAndarAlaDescricao());
			vo.setUnfSeq(unidadeFuncional.getSeq());
			vo.setSigla(indicadorHospitalar.getEspecialidade() == null ? null : indicadorHospitalar
					.getEspecialidade().getSigla());
			vo.setCapacReferencial(this.verificarNullValueDouble(indicadorHospitalar
					.getCapacidadeReferencial()));
			vo.setMediaPacienteDia(this.verificarNullValueDouble(indicadorHospitalar
					.getMediaPacienteDia()));
			Long pacienteMesAnterior = this.verificarNullValueLong(indicadorHospitalar
					.getTotalSaldo())
					+ this.verificarNullValueLong(indicadorHospitalar.getTotalAltas())
					+ this.verificarNullValueLong(indicadorHospitalar.getTotalObitosMais48h())
					+ this.verificarNullValueLong(indicadorHospitalar.getTotalObitosMenos48h());
			pacienteMesAnterior = pacienteMesAnterior
					- (this.verificarNullValueLong(indicadorHospitalar.getTotalInternacoesMes()) + this
							.verificarNullValueLong(indicadorHospitalar.getTotalIntAreaSatelite()));
			vo.setPacientesMesAnterior(pacienteMesAnterior);
			vo.setTotAltas(this.verificarNullValueLong(indicadorHospitalar.getTotalAltas()));
			vo.setTotEntradasInternacoes(this.verificarNullValueLong(indicadorHospitalar
					.getTotalInternacoesMes()));
			vo.setTotEntradasOutrasEspecialidades(this.verificarNullValueLong(indicadorHospitalar
					.getTotalEntreOutrasEspecialidades()));
			vo.setTotEntradasOutrasUnidades(this.verificarNullValueLong(indicadorHospitalar
					.getTotalEntreOutrasClinicas())
					+ this.verificarNullValueLong(indicadorHospitalar.getTotalEntreOutrasUnidades()));
			vo.setTotInternacoesMes(this.verificarNullValueLong(indicadorHospitalar
					.getTotalInternacoesMes()));
			vo.setTotInternacoesAreaSatelite(this.verificarNullValueLong(indicadorHospitalar
					.getTotalIntAreaSatelite()));
			vo.setTotObitosMais24hs(this.verificarNullValueLong(indicadorHospitalar
					.getTotalObitosMais48h()));

			// Calcular o total de bloqueados
			Calendar cal = Calendar.getInstance();
			cal.setTime(mesCompetencia);
			int ultimoDiaMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			BigDecimal totBloqueios = BigDecimal.valueOf(
					indicadorHospitalar.getTotalBloqueios().intValue()).divide(
					BigDecimal.valueOf(ultimoDiaMes), 2, BigDecimal.ROUND_HALF_UP);
			vo.setTotBloqueios(totBloqueios.doubleValue());

			vo.setTotObitosMenos24hs(this.verificarNullValueLong(indicadorHospitalar
					.getTotalObitosMenos48h()));
			vo.setTotSaidas(this.verificarNullValueLong(indicadorHospitalar.getTotalSaidas()));
			vo.setTotSaidaOutrasEspecialidades(this.verificarNullValueLong(indicadorHospitalar
					.getTotalSaidaOutrasEspecialidades()));
			vo.setTotSaidaOutrasUnidades(this.verificarNullValueLong(indicadorHospitalar
					.getTotalSaidaOutrasClinicas())
					+ this.verificarNullValueLong(indicadorHospitalar.getTotalSaidaOutrasUnidades()));
			vo.setTotSaldo(this.verificarNullValueLong(indicadorHospitalar.getTotalSaldo()));
			vo.setPacHospitalDia(this.verificarNullValueDouble(indicadorHospitalar.getPacienteDia()));
			vo.setLeitoDia(this.verificarNullValueLong(indicadorHospitalar.getLeitoDia()));
			vo.setPercentualOcupacao(this.verificarNullValueDouble(indicadorHospitalar
					.getPercentualOcupacao()));
			vo.setMediaPermanencia(this.verificarNullValueDouble(indicadorHospitalar
					.getMediaPermanencia()));
			vo.setIndiceMortGeral(this.verificarNullValueDouble(indicadorHospitalar
					.getIndiceMorteGeral()));
			vo.setIndiceMortEspecialidade(this.verificarNullValueDouble(indicadorHospitalar
					.getIndiceMorteEspecifico()));
			vo.setIndiceIntervaloSubstituicao(this.verificarNullValueDouble(indicadorHospitalar
					.getIndiceIntervaloSubstituicao()));
			vo.setIndiceRenovacao(this.verificarNullValueDouble(indicadorHospitalar
					.getIndiceRenovacao()));
			vo.setTotEntradas(this.verificarNullValueLong(indicadorHospitalar
					.getTotalInternacoesMes())
					+ this.verificarNullValueLong(indicadorHospitalar.getTotalIntAreaSatelite()));

			retorno.add(vo);
		}

		return retorno;
	}

	private Long verificarNullValueLong(Object valor) {
		if (valor == null) {
			return Long.valueOf(0);
		} else {
			return Long.valueOf(valor.toString());
		}
	}

	private Double verificarNullValueDouble(Object valor) {
		if (valor == null) {
			return Double.valueOf(0);
		} else {
			return Double.valueOf(valor.toString());
		}
	}

	public UnidadeIndicadoresVO formatarValoresTotaisIndicadoresUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia) {

		UnidadeIndicadoresVO vo = null;
		List<UnidadeIndicadoresVO> retorno = new ArrayList<UnidadeIndicadoresVO>();

		List<AinIndicadoresHospitalares> indicadoresUnidade = getInternacaoFacade()
				.pesquisarIndicadoresTotaisUnidade(tipoUnidade, mesCompetencia);

		// Loop para popular lista. Calculos serão aplicados posteriormente
		for (AinIndicadoresHospitalares indicadorHospitalar : indicadoresUnidade) {
			vo = new UnidadeIndicadoresVO();

			vo.setCompetenciaInternacao(indicadorHospitalar.getCompetenciaInternacao());
			Long pacienteMesAnterior = indicadorHospitalar.getTotalSaldo().longValue()
					+ indicadorHospitalar.getTotalAltas().longValue()
					+ indicadorHospitalar.getTotalObitosMais48h().longValue()
					+ indicadorHospitalar.getTotalObitosMenos48h().longValue();
			pacienteMesAnterior = pacienteMesAnterior
					- (indicadorHospitalar.getTotalInternacoesMes().longValue() + indicadorHospitalar
							.getTotalIntAreaSatelite().longValue());
			vo.setPacientesMesAnterior(pacienteMesAnterior);
			vo.setTotAltas(indicadorHospitalar.getTotalAltas().longValue());
			vo.setTotEntradasInternacoes(indicadorHospitalar.getTotalInternacoesMes().longValue());
			vo.setTotEntradasOutrasEspecialidades(Long.valueOf(0));
			vo.setTotEntradasOutrasUnidades(Long.valueOf(0));
			vo.setTotInternacoesMes(indicadorHospitalar.getTotalInternacoesMes().longValue());
			vo.setTotInternacoesAreaSatelite(indicadorHospitalar.getTotalIntAreaSatelite()
					.longValue());
			vo.setTotObitosMais24hs(indicadorHospitalar.getTotalObitosMais48h().longValue());
			vo.setTotObitosMenos24hs(indicadorHospitalar.getTotalObitosMenos48h().longValue());
			vo.setTotSaidas(indicadorHospitalar.getTotalAltas().longValue()
					+ indicadorHospitalar.getTotalObitosMais48h().longValue()
					+ indicadorHospitalar.getTotalObitosMenos48h().longValue());
			vo.setTotSaidaOutrasEspecialidades(Long.valueOf(0));
			vo.setTotSaidaOutrasUnidades(Long.valueOf(0));
			vo.setTotSaldo(indicadorHospitalar.getTotalSaldo().longValue());
			vo.setPacHospitalDia(indicadorHospitalar.getPacienteDia().doubleValue());
			vo.setTotBloqueios(indicadorHospitalar.getTotalBloqueios().doubleValue());
			vo.setLeitoDia(indicadorHospitalar.getLeitoDia().longValue());

			retorno.add(vo);
		}

		vo = new UnidadeIndicadoresVO();
		this.agruparValoresIndicadoresTotaisUnidade(retorno, vo);

		// Ajuste no valor da capacidade referencial, pois a mesma não estava
		// desconsiderando o total de bloqueios
		vo.setCapacReferencial(vo.getCapacReferencial() - vo.getTotBloqueios());

		return vo;
	}

	private void agruparValoresIndicadoresTotaisUnidade(List<UnidadeIndicadoresVO> voList,
			UnidadeIndicadoresVO vo) {
		final String descricaoTotalGeral = "Total Geral";
		final String sigla = "ZZZ";

		for (UnidadeIndicadoresVO indicadoresVO : voList) {
			vo.setCompetenciaInternacao(indicadoresVO.getCompetenciaInternacao());
			vo.setPacientesMesAnterior(this.verificarNullValueLong(vo.getPacientesMesAnterior())
					+ indicadoresVO.getPacientesMesAnterior());
			vo.setTotAltas(this.verificarNullValueLong(vo.getTotAltas())
					+ indicadoresVO.getTotAltas());
			vo.setTotEntradasInternacoes(this.verificarNullValueLong(vo.getTotEntradasInternacoes())
					+ indicadoresVO.getTotEntradasInternacoes());
			vo.setTotInternacoesMes(this.verificarNullValueLong(vo.getTotInternacoesMes())
					+ indicadoresVO.getTotInternacoesMes());
			vo.setTotInternacoesAreaSatelite(this.verificarNullValueLong(vo
					.getTotInternacoesAreaSatelite())
					+ indicadoresVO.getTotInternacoesAreaSatelite());
			vo.setTotObitosMais24hs(this.verificarNullValueLong(vo.getTotObitosMais24hs())
					+ indicadoresVO.getTotObitosMais24hs());
			vo.setTotObitosMenos24hs(this.verificarNullValueLong(vo.getTotObitosMenos24hs())
					+ indicadoresVO.getTotObitosMenos24hs());
			vo.setTotSaidas(this.verificarNullValueLong(vo.getTotSaidas())
					+ indicadoresVO.getTotSaidas());
			vo.setTotSaldo(this.verificarNullValueLong(vo.getTotSaldo())
					+ indicadoresVO.getTotSaldo());
			vo.setTotBloqueios(this.verificarNullValueDouble(vo.getTotBloqueios())
					+ indicadoresVO.getTotBloqueios());
			vo.setPacHospitalDia(this.verificarNullValueDouble(vo.getPacHospitalDia())
					+ indicadoresVO.getPacHospitalDia());
			vo.setLeitoDia(this.verificarNullValueLong(vo.getLeitoDia())
					+ indicadoresVO.getLeitoDia());
		}

		vo.setTipo(Long.valueOf(2));
		vo.setAndarAlaDescricao(descricaoTotalGeral);
		vo.setUnfSeq(Short.valueOf("9999"));
		vo.setSigla(sigla);

		if (voList != null && voList.size() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(vo.getCompetenciaInternacao());
			Integer ultimoDiaMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			// Setados doubleValues senão não trazia casas decimais
			vo.setCapacReferencial(new Double(vo.getLeitoDia().doubleValue()
					/ ultimoDiaMes.doubleValue()));
			vo.setTotBloqueios(vo.getTotBloqueios().doubleValue() / ultimoDiaMes.doubleValue());
			vo.setMediaPacienteDia(vo.getPacHospitalDia().doubleValue()
					/ ultimoDiaMes.doubleValue());

			this.calcularIndices(vo);
		}
	}

	private void calcularIndices(UnidadeIndicadoresVO vo) {
		// PERCENTUAL_OCUPACAO
		vo.setPercentualOcupacao(this.calcularPercentualOcupacao(vo.getLeitoDia(),
				vo.getPacHospitalDia()));

		// MEDIA_PERMANENCIA
		vo.setMediaPermanencia(this.calcularMediaPermanencia(vo.getTotAltas(),
				vo.getTotObitosMais24hs(), vo.getTotObitosMenos24hs(), vo.getPacHospitalDia()));

		// INDICE_MORT_GERAL
		vo.setIndiceMortGeral(this.calcularIndiceMortalidadeGeral(vo.getTotAltas(),
				vo.getTotObitosMais24hs(), vo.getTotObitosMenos24hs()));

		// INDICE_MORT_ESPECIALIDADE
		vo.setIndiceMortEspecialidade(this.calcularIndiceMortalidadeEspecialidade(vo.getTotAltas(),
				vo.getTotObitosMais24hs(), vo.getTotObitosMenos24hs()));

		// INDICE_INTERVALO_SUBSTITUICAO
		vo.setIndiceIntervaloSubstituicao(this.calcularIndiceIntervaloSubstituicao(
				vo.getTotAltas(), vo.getTotObitosMais24hs(), vo.getTotObitosMenos24hs(),
				vo.getPacHospitalDia(), vo.getLeitoDia(), vo.getMediaPermanencia(),
				vo.getPercentualOcupacao()));

		// INDICE_RENOVACAO
		vo.setIndiceRenovacao(this.calcularIndiceRenovacao(vo.getTotAltas(),
				vo.getTotObitosMais24hs(), vo.getTotObitosMenos24hs(), vo.getCapacReferencial()));

		// TOT_ENTRADAS
		vo.setTotEntradas(vo.getTotInternacoesMes() + vo.getTotInternacoesAreaSatelite());
	}

	/**
	 * Método para calcular o percentual de ocupação da unidade
	 * 
	 * @param leitoDia
	 * @param pacHospitalDia
	 */
	private Double calcularPercentualOcupacao(Long leitoDia, Double pacHospitalDia) {
		if (leitoDia == null) {
			return Double.valueOf(0);
		} else {
			return new Double(pacHospitalDia * 100 / leitoDia);
		}
	}

	/**
	 * Método para calcular a média de permanencia da unidade
	 * 
	 * @param totAltas
	 * @param totObitosMais24hs
	 * @param totObitosMenos24hs
	 * @param pacHospitalDia
	 * @return
	 */
	private Double calcularMediaPermanencia(Long totAltas, Long totObitosMais24hs,
			Long totObitosMenos24hs, Double pacHospitalDia) {
		Long somaValores = totAltas + totObitosMais24hs + totObitosMenos24hs;

		if (somaValores == 0) {
			return Double.valueOf(0);
		} else {
			return new Double(pacHospitalDia / somaValores);
		}
	}

	/**
	 * Método para calcular o indice de mortalidade geral da unidade
	 * 
	 * @param totAltas
	 * @param totObitosMais24hs
	 * @param totObitosMenos24hs
	 * @return
	 */
	private Double calcularIndiceMortalidadeGeral(Long totAltas, Long totObitosMais24hs,
			Long totObitosMenos24hs) {
		Long somaValores = totAltas + totObitosMais24hs + totObitosMenos24hs;

		if (somaValores == 0) {
			return Double.valueOf(0);
		} else {
			Double indiceMortalidade = new Double(
					(totObitosMais24hs.doubleValue() + totObitosMenos24hs.doubleValue()) * 100
							/ somaValores.doubleValue());
			return indiceMortalidade;
		}
	}

	/**
	 * Método para calcular o indice de mortalidade por especialidade da unidade
	 * 
	 * @param totAltas
	 * @param totObitosMais24hs
	 * @param totObitosMenos24hs
	 * @return
	 */
	private Double calcularIndiceMortalidadeEspecialidade(Long totAltas, Long totObitosMais24hs,
			Long totObitosMenos24hs) {
		Long somaValores = totAltas + totObitosMais24hs + totObitosMenos24hs;

		if (somaValores == 0) {
			return Double.valueOf(0);
		} else {
			Double indiceMortalidadeEspecialidade = new Double(
					(totObitosMais24hs.doubleValue() * 100) / somaValores.doubleValue());
			return indiceMortalidadeEspecialidade;
		}
	}

	/**
	 * Método para calcular o indice de intervalo de substituição da unidade
	 * 
	 * @param totAltas
	 * @param totObitosMais24hs
	 * @param totObitosMenos24hs
	 * @param pacHospitalDia
	 * @param leitoDia
	 * @param mediaPermanencia
	 * @param percentualOcupacao
	 * @return
	 */
	private Double calcularIndiceIntervaloSubstituicao(Long totAltas, Long totObitosMais24hs,
			Long totObitosMenos24hs, Double pacHospitalDia, Long leitoDia, Double mediaPermanencia,
			Double percentualOcupacao) {
		Long somaValores = totAltas + totObitosMenos24hs + totObitosMais24hs;

		if (pacHospitalDia == 0 || leitoDia == 0 || somaValores == 0) {
			return Double.valueOf(0);
		} else {
			Double percentualDesocupacao = 100 - pacHospitalDia * 100 / leitoDia.doubleValue();
			Double indiceIntervaloSubstituicao = (percentualDesocupacao * mediaPermanencia)
					/ percentualOcupacao;
			return indiceIntervaloSubstituicao;
		}
	}

	/**
	 * Método para calcular o indice de renovação da unidade
	 * 
	 * @param totAltas
	 * @param totObitosMais24hs
	 * @param totObitosMenos24hs
	 * @param capacReferencial
	 * @param vo
	 * @return
	 */
	private Double calcularIndiceRenovacao(Long totAltas, Long totObitosMais24hs,
			Long totObitosMenos24hs, Double capacReferencial) {
		if (capacReferencial == 0) {
			return Double.valueOf(0);
		} else {
			Long somaValores = totAltas + totObitosMenos24hs + totObitosMais24hs;
			Double indiceRenovacao = somaValores / capacReferencial;
			return indiceRenovacao;
		}
	}

	/**
	 * Método para pesquisar os indicadores de unidade
	 * 
	 * @param tipoUnidade
	 * @param mesCompetencia
	 * @return
	 */
	public List<UnidadeIndicadoresVO> pesquisarIndicadoresUnidade(DominioTipoUnidade tipoUnidade,
			Date mesCompetencia) {

		List<UnidadeIndicadoresVO> indicadoresUnidade = new ArrayList<UnidadeIndicadoresVO>();

		indicadoresUnidade.addAll(this.formatarValoresGeraisIndicadoresUnidade(tipoUnidade,
				mesCompetencia));
		// Ordena lista por descricao da unidade e por sigla da especialidade
		Collections.sort(indicadoresUnidade, new IndicadorUnidadeComparator());
		indicadoresUnidade.add(this.formatarValoresTotaisIndicadoresUnidade(tipoUnidade,
				mesCompetencia));

		return indicadoresUnidade;
	}
	
	/*
	public void pesquisarIndicadoresUnidade() {
		Calendar mesCompetencia = Calendar.getInstance();
		mesCompetencia.set(2010, 02, 01);

		DominioTipoUnidade tipoUnidade = DominioTipoUnidade.U;

		List<UnidadeIndicadoresVO> indicadoresUnidade = this.pesquisarIndicadoresUnidade(
				tipoUnidade, mesCompetencia.getTime());

		// Teste para mostrar valores
		for (UnidadeIndicadoresVO indicadoresUnidadeVO : indicadoresUnidade) {
			logInfo(indicadoresUnidadeVO.getValoresAtributos());
		}
	}
	*/

	/**
	 * Inner Class para ordenar a lista dos indicadores de unidade pela
	 * descrição da unidade (andar, ala, descrição) e pela sigla da
	 * especialidade. A especialidade que tiver o valor null será considerada a
	 * maior de todas especialidades de sua unidade.
	 */
	class IndicadorUnidadeComparator implements Comparator<UnidadeIndicadoresVO> {

		@Override
		public int compare(UnidadeIndicadoresVO o1, UnidadeIndicadoresVO o2) {

			String unidade1 = ((UnidadeIndicadoresVO) o1).getAndarAlaDescricao();
			String unidade2 = ((UnidadeIndicadoresVO) o2).getAndarAlaDescricao();

			int comparacaoUnidades = unidade1.compareTo(unidade2);

			if (comparacaoUnidades != 0) {
				return comparacaoUnidades;
			} else {
				String especialidade1 = ((UnidadeIndicadoresVO) o1).getSigla();
				String especialidade2 = ((UnidadeIndicadoresVO) o2).getSigla();

				// Especialidade == null precisa ser a última da lista no seu
				// criterio de ordenação
				if (especialidade1 == null) {
					return 1;
				} else if (especialidade2 == null) {
					return -1;
				} else {
					return especialidade1.compareTo(especialidade2);
				}
			}
		}
	}

	/**
	 * @param mes
	 * @return
	 */
	public Integer obterNumeroOcorrenciasIndicadoresGerais(Date mes) {
		return getInternacaoFacade().obterNumeroOcorrenciasIndicadoresGerais(mes);
	}

	/**
	 * @param tipoUnidade
	 * @param mes
	 * @return
	 */
	public Integer obterNumeroOcorrenciasIndicadoresUnidade(DominioTipoUnidade tipoUnidade, Date mes) {
		return getInternacaoFacade().obterNumeroOcorrenciasIndicadoresUnidade(
				tipoUnidade, mes);
	}

}
