package br.gov.mec.aghu.internacao.pesquisa.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.indicadores.business.IIndicadoresFacade;
import br.gov.mec.aghu.internacao.dao.VIndIntPacienteDiaDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaAptosNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaCtiNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaInternadosNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaOutrasClinicasNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaOutrasUnidadesNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.GlobalReferencialEspecialidadeProfissonalVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ReferencialEspecialidadeProfissonalGridVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ReferencialEspecialidadeProfissonalViewVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesquisaReferencialEspecialidadeProfissionalON extends BaseBusiness {


@EJB
private PesquisaInternacaoRN pesquisaInternacaoRN;

@EJB
private PesquisaReferencialEspecialidadeProfissionalRN pesquisaReferencialEspecialidadeProfissionalRN;

private static final Log LOG = LogFactory.getLog(PesquisaReferencialEspecialidadeProfissionalON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IIndicadoresFacade indicadoresFacade;

@Inject
private VIndIntPacienteDiaDAO vIndIntPacienteDiaDAO;

@EJB
private AinkPesRefPro ainkPesRefPro;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3586119643520572390L;

	private enum PesquisaReferencialEPONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_INFORMAR_ESPECIALIDADE;
	}
	
	public void validaDadosPesquisaReferencialEspecialidadeProfissional(AghEspecialidades especialidade) throws ApplicationBusinessException {
		if (!(especialidade != null && especialidade.getSeq() != null)) {
			throw new ApplicationBusinessException(PesquisaReferencialEPONExceptionCode.MENSAGEM_INFORMAR_ESPECIALIDADE);
		}
	}

	public Long pesquisarReferencialEspecialidadeProfissonalGridVOCount(AghEspecialidades especialidade)
			throws ApplicationBusinessException {

		this.validaDadosPesquisaReferencialEspecialidadeProfissional(especialidade);

		return this.getPesquisaReferencialEspecialidadeProfissionalRN().pesquisarReferencialEspecialidadeProfissonalGridVOCount(
				especialidade);
	}

	/**
	 * Conversão da pesquisa sobre a view V_AIN_PES_REF_ESP_PRO, utilizada na
	 * funcionalidade "Pesquisar referencial Especialidade/Profissional".
	 * 
	 * @return
	 */
	@Secure("#{s:hasPermission('referencialEspecialidadeProfissional','pesquisar')}")
	public List<ReferencialEspecialidadeProfissonalGridVO> pesquisarReferencialEspecialidadeProfissonalGridVO(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AghEspecialidades especialidade) throws ApplicationBusinessException {

		validaDadosPesquisaReferencialEspecialidadeProfissional(especialidade);

		List<Object[]> resultListView = getPesquisaReferencialEspecialidadeProfissionalRN()
				.pesquisarReferencialEspecialidadeProfissonalGridVO(firstResult, maxResult, orderProperty, asc, especialidade);

		return criarListaReferencialEspecialidadeProfissionalGridVO(resultListView);
	}

	private List<ReferencialEspecialidadeProfissonalGridVO> criarListaReferencialEspecialidadeProfissionalGridVO(
			List<Object[]> resultListView) {
		ReferencialEspecialidadeProfissonalViewVO referencialEPViewVO = null;
		ReferencialEspecialidadeProfissonalGridVO referencialEPGridVO = null;
		List<ReferencialEspecialidadeProfissonalGridVO> listaGridVO = null;

		listaGridVO = new ArrayList<ReferencialEspecialidadeProfissonalGridVO>(
				resultListView.size());

		for (Object[] itemView : resultListView) {
			referencialEPViewVO = criarReferencialEspecialidadeProfissonalViewVO(itemView);
			referencialEPGridVO = criarReferencialEspecialidadeProfissonalGridVO(referencialEPViewVO);

			listaGridVO.add(referencialEPGridVO);
		}

		return listaGridVO;
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private ReferencialEspecialidadeProfissonalGridVO criarReferencialEspecialidadeProfissonalGridVO(
			ReferencialEspecialidadeProfissonalViewVO referencialEPViewVO) {

		ReferencialEspecialidadeProfissonalGridVO referencialEPGridVO = null;
		referencialEPGridVO = new ReferencialEspecialidadeProfissonalGridVO(
				referencialEPViewVO);

		Short espSeq = referencialEPGridVO.getEspSeq();
		Short espVinculo = referencialEPGridVO.getEspVinculo();
		Integer espMatricula = referencialEPGridVO.getEspMatricula();

		GlobalReferencialEspecialidadeProfissonalVO globalVO = new GlobalReferencialEspecialidadeProfissonalVO();

		Integer pac = null;
		Long bloqueios = null;
		Long diferenca = null;
		Integer referencial = null;
		Integer cti = null;
		Integer ctiAsat = null;
		Integer ctiAdm = null;
		Integer aptos = null;
		Integer aptosAsat = null;
		Integer aptosAdm = null;
		Integer outrasUnidades = null;
		Integer outrasUnidadesAdm = null;
		Integer outrasUnidadesAsat = null;
		Integer outrasClinicas = null;
		Integer outrasClinicasAdm = null;
		Integer outrasClinicasAsat = null;
		Integer total = null;
		Integer totPac = null;
		Long totBloqueio = null;
		Integer totRefer = null;
		Long totDif = null;
		Integer totCti = null;
		Integer totAptos = null;
		Integer totUnids = null;
		Integer totClinicas = null;
		Integer totTotal = null;
		Integer pacAdm = null;
		Integer pacAsat = null;
		Integer totPacAdm = null;
		Integer totPacAsat = null;

		total = 0;
		aptos = 0;
		outrasUnidades = 0;
		outrasClinicas = 0;

		/* Populando nome do profissional e Crm como lookup */
		if (referencialEPGridVO.getEspVinculo() != 0) {
			Short eVinculo = referencialEPGridVO.getEspVinculo();
			Integer eMatricula = referencialEPGridVO.getEspMatricula();

			String nome = this.getPesquisaInternacaoRN().buscarNomeUsual(eVinculo,
					eMatricula);
			String nroRegConselho = this.getPesquisaInternacaoRN()
					.buscarNroRegistroConselho(eVinculo, eMatricula);

			referencialEPGridVO.setEquipe(nome);
			referencialEPGridVO.setCrm(nroRegConselho);
		} else {
			referencialEPGridVO.setEquipe("TOTAL");
			referencialEPGridVO.setCrm("-----");
		}

		/* Calculos para popular campos da tela por especialidades */

		ContaInternadosNewVO cinVO = null;
		cinVO = this.getAinkPesRefPro().contaInternadosNew(espVinculo, espMatricula,
				espSeq);
		total = cinVO.getPac();
		pacAdm = cinVO.getAdm();
		pacAsat = cinVO.getAst();

		bloqueios = this.getAinkPesRefPro().contaBloqueios(espVinculo, espMatricula,
				espSeq);

		referencialEPGridVO.setBlq(bloqueios == null ? "" : bloqueios
				.toString());

		referencial = referencialEPGridVO.getCapacReferencial();

		ContaCtiNewVO contaCtiNewVO = null;
		contaCtiNewVO = this.getAinkPesRefPro().contaCtiNew(espVinculo,
				espMatricula, espSeq);
		cti = contaCtiNewVO.getCti();
		ctiAdm = contaCtiNewVO.getAdm();
		ctiAsat = contaCtiNewVO.getAst();

		referencialEPGridVO.setCti(cti == null ? "" : cti.toString());

		ContaAptosNewVO contaAptosNewVO = null;
		contaAptosNewVO = this.getAinkPesRefPro().contaAptosNew(espVinculo,
				espMatricula, espSeq);

		aptos = contaAptosNewVO.getAptos();
		aptosAdm = contaAptosNewVO.getAptosAdm();
		aptosAsat = contaAptosNewVO.getAptosAsat();

		referencialEPGridVO.setAptos(aptos == null ? "" : aptos.toString());

		ContaOutrasUnidadesNewVO contaOutrasUnidadesNewVO = null;
		contaOutrasUnidadesNewVO = this.getAinkPesRefPro().contaOutrasUnidadesNew(
				espVinculo, espMatricula, espSeq);
		outrasUnidades = contaOutrasUnidadesNewVO.getOutrasUnidades();
		outrasUnidadesAdm = contaOutrasUnidadesNewVO.getAdm();
		outrasUnidadesAsat = contaOutrasUnidadesNewVO.getAst();

		referencialEPGridVO.setOutrasUn(outrasUnidades == null ? ""
				: outrasUnidades.toString());

		ContaOutrasClinicasNewVO contaOutrasClinicasNewVO = null;
		contaOutrasClinicasNewVO = this.getAinkPesRefPro().contaOutrasClinicasNew(
				espVinculo, espMatricula, espSeq);
		outrasClinicas = contaOutrasClinicasNewVO.getOutrasClinicas();
		outrasClinicasAdm = contaOutrasClinicasNewVO.getAdm();
		outrasClinicasAsat = contaOutrasClinicasNewVO.getAst();

		referencialEPGridVO.setOutrasClin(outrasClinicas == null ? ""
				: outrasClinicas.toString());

		pac = total - cti - aptos - outrasUnidades - outrasClinicas;
		pacAdm = pacAdm - ctiAdm - aptosAdm - outrasUnidadesAdm
				- outrasClinicasAdm;
		pacAsat = pacAsat - ctiAsat - aptosAsat - outrasUnidadesAsat
				- outrasClinicasAsat;

		referencialEPGridVO.setPac(pac == null ? "" : pac.toString());
		referencialEPGridVO.setPacElet(pacAdm == null ? "" : pacAdm.toString());
		referencialEPGridVO
				.setPacUrg(pacAsat == null ? "" : pacAsat.toString());
		referencialEPGridVO.setTotal(total == null ? "" : total.toString());

		diferenca = pac - (referencial - bloqueios);

		referencialEPGridVO.setDif(diferenca == null ? "" : diferenca
				.toString());

		/* Montando a linha de totais - Tot */
		if (!"TOTAL".equals(referencialEPGridVO.getEquipe())) {
			totPac = NumberUtils.toInt(globalVO.getPac());
			totPac = totPac + pac;
			globalVO.setPac(totPac.toString());

			totPacAdm = NumberUtils.toInt(globalVO.getPacAdm());
			totPacAdm = totPacAdm + pacAdm;
			globalVO.setPacAdm(totPacAdm.toString());

			totPacAsat = NumberUtils.toInt(globalVO.getPacAsat());
			totPacAsat = totPacAsat + pacAsat;
			globalVO.setPacAsat(totPacAsat.toString());

			totBloqueio = NumberUtils.toLong(globalVO.getBloqueio());
			totBloqueio = totBloqueio + bloqueios;
			globalVO.setBloqueio(totBloqueio.toString());

			totRefer = NumberUtils.toInt(globalVO.getReferencial());
			totRefer = totRefer + referencial;
			globalVO.setReferencial(totRefer.toString());

			totCti = NumberUtils.toInt(globalVO.getCti());
			totCti = totCti + cti;
			globalVO.setCti(totCti.toString());

			totAptos = NumberUtils.toInt(globalVO.getAptos());
			totAptos = totAptos + aptos;
			globalVO.setAptos(totAptos.toString());

			totUnids = NumberUtils.toInt(globalVO.getUnidades());
			totUnids = totUnids + outrasUnidades;
			globalVO.setUnidades(totUnids.toString());

			totClinicas = NumberUtils.toInt(globalVO.getClinicas());
			totClinicas = totClinicas + outrasClinicas;
			globalVO.setClinicas(totClinicas.toString());
			// copy(to_char(v_tot_unids),'global.g_unidades');

			totTotal = NumberUtils.toInt(globalVO.getTotal());
			totTotal = totTotal + total;
			globalVO.setTotal(totTotal.toString());
		} else {
			/* populando totais na tela */
			referencialEPGridVO.setPac(globalVO.getPac());
			referencialEPGridVO.setPacElet(globalVO.getPacAdm());
			referencialEPGridVO.setPacUrg(globalVO.getPacAsat());
			referencialEPGridVO.setBlq(globalVO.getBloqueio());

			totPac = NumberUtils.toInt(globalVO.getPac());
			totRefer = NumberUtils.toInt(globalVO.getReferencial());
			totBloqueio = NumberUtils.toLong(globalVO.getBloqueio());
			totDif = totPac - (totRefer - totBloqueio);
			referencialEPGridVO.setBlq(totDif.toString());

			referencialEPGridVO.setCti(globalVO.getCti());
			referencialEPGridVO.setAptos(globalVO.getAptos());
			referencialEPGridVO.setOutrasUn(globalVO.getUnidades());
			referencialEPGridVO.setOutrasClin(globalVO.getClinicas());

			referencialEPGridVO.setTotal(globalVO.getTotal());
		}

		return referencialEPGridVO;
	}

	/**
	 * Cria e popula os campos do VO utilizados na view V_AIN_PES_REF_ESP_PRO, a
	 * partir das colunas projetadas na pesquisa.
	 * 
	 * @param itemView
	 *            Objeto com os dados de origem.
	 * @return VO populado.
	 */
	private ReferencialEspecialidadeProfissonalViewVO criarReferencialEspecialidadeProfissonalViewVO(
			Object[] itemView) {
		ReferencialEspecialidadeProfissonalViewVO referencialEPViewVO = new ReferencialEspecialidadeProfissonalViewVO();

		Integer ordem = 1;
		Short espSeq = (Short) itemView[0];
		Short espVinculo = (Short) itemView[1];
		Integer espMatricula = (Integer) itemView[2];
		Integer capacReferencial = (Integer) itemView[3];
		
		// TODO Alterar chamadas para fazer cálculos com base na VIEW
		Object[] valores = this.getVIndIntPacienteDiaDAO().obterPacienteDia(espMatricula, espVinculo, espSeq);
		
		BigDecimal mediaPermanencia = BigDecimal.ZERO;
		BigDecimal mediaPacienteDia = BigDecimal.ZERO;
		
		if (valores != null) {
			mediaPermanencia = this.obterMediaPermanencia(valores);
			mediaPacienteDia = this.obterMediaPacienteDia(valores);
		}
		
		referencialEPViewVO.setOrdem(ordem);
		referencialEPViewVO.setEspSeq(espSeq);
		referencialEPViewVO.setEspVinculo(espVinculo);
		referencialEPViewVO.setEspMatricula(espMatricula);
		referencialEPViewVO.setCapacReferencial(capacReferencial);
		referencialEPViewVO.setInhMediaPermanencia(mediaPermanencia);
		//referencialEPViewVO.setInhPercentualOcupacao(percentualOcupacao);
		referencialEPViewVO.setInhMediaPacienteDia(mediaPacienteDia);

		return referencialEPViewVO;
	}
	
	/**
	 * Método para calcular a Media de Permanencia
	 * 
	 * @param valores
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private BigDecimal obterMediaPermanencia(Object[] valores) {
		Double pacienteDia = valores[3] == null ? Double.valueOf("0") : (Double) valores[3];
		Long saidasEspecialidade = valores[4] == null ? 0L : (Long) valores[4];
		Long saidasClinica = valores[5] == null ? 0L : (Long) valores[5];
		Long saidasEquipe = valores[6] == null ? 0L : (Long) valores[6];
		Long saidasUnidade = valores[7] == null ? 0L : (Long) valores[7];
		Integer saidas = valores[8] == null ? 0 : ((Long) valores[8]).intValue();
		
		Long totalSaidas = saidasEspecialidade + saidasClinica + saidasEquipe + saidasUnidade + saidas;
		
		BigDecimal mediaPermanencia = BigDecimal.ZERO;
		
		if (totalSaidas != null && !totalSaidas.equals(0L)) {
			mediaPermanencia = new BigDecimal(pacienteDia).divide(new BigDecimal(totalSaidas), 2, RoundingMode.HALF_UP);	
		}
		
		return mediaPermanencia;
	}
	
	/**
	 * Método para calcular a Media Paciente Dia, com base no ultimo mês em que dados foram gerados.
	 * 
	 * @param valores
	 * @return
	 */
	private BigDecimal obterMediaPacienteDia(Object[] valores) {
		Double pacienteDia = (Double) valores[3];
		
		Date data = this.getIndicadorFacade().obterUltimaDataInicial();
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		Integer numeroDiasMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		BigDecimal mediaPacienteDia = new BigDecimal(pacienteDia).divide(new BigDecimal(numeroDiasMes), 2, RoundingMode.HALF_UP);
		
		return mediaPacienteDia;
	}
	
	
	protected PesquisaInternacaoRN getPesquisaInternacaoRN(){
		return pesquisaInternacaoRN;
	}
	
	protected PesquisaReferencialEspecialidadeProfissionalRN getPesquisaReferencialEspecialidadeProfissionalRN(){
		return pesquisaReferencialEspecialidadeProfissionalRN;
	}

	protected AinkPesRefPro getAinkPesRefPro(){
		return ainkPesRefPro;
	}
	
	protected VIndIntPacienteDiaDAO getVIndIntPacienteDiaDAO() {
		return vIndIntPacienteDiaDAO;
	}

	protected IIndicadoresFacade getIndicadorFacade() {
		return this.indicadoresFacade;
	}

}