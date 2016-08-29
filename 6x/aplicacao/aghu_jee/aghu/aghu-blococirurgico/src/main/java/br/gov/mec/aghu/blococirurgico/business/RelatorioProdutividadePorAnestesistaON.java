package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProdutividadePorAnestesistaConsultaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProdutividadePorAnestesistaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioProdutividadePorAnestesistaON extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	private static final Log LOG = LogFactory.getLog(RelatorioProdutividadePorAnestesistaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	private static final long serialVersionUID = -6088787772346529373L;

	public List<RelatorioProdutividadePorAnestesistaVO> listarProdutividadePorAnestesista(AghUnidadesFuncionais unidadeCirurgica, 
			DominioFuncaoProfissional funcaoProfissional, Date dataInicial, Date dataFinal){
		
		List<RelatorioProdutividadePorAnestesistaVO> listRetorno = new ArrayList<RelatorioProdutividadePorAnestesistaVO>();
		List<DominioFuncaoProfissional> listDominioFuncaoProfissional = preencherListDominioFuncaoProfissional(funcaoProfissional);
		List<RelatorioProdutividadePorAnestesistaConsultaVO> listC2 = new ArrayList<RelatorioProdutividadePorAnestesistaConsultaVO>();
		listC2 = getMbcProfCirurgiasDAO().listarCirurgiasProdutividadePorAnestesista(unidadeCirurgica.getSeq(), dataInicial, dataFinal, listDominioFuncaoProfissional);
		tratarC2(listC2, dataInicial, dataFinal);
		setarCamposC2(unidadeCirurgica.getSeq(), dataInicial, dataFinal, listC2, listRetorno);
		for (RelatorioProdutividadePorAnestesistaVO retorno : listRetorno) {
			List<RelatorioProdutividadePorAnestesistaConsultaVO> listC3 = new ArrayList<RelatorioProdutividadePorAnestesistaConsultaVO>();
			listC3 = getMbcAnestesiaCirurgiasDAO().listarCirurgiasProdutividadeAnestesiaTipo(unidadeCirurgica.getSeq(), dataInicial, dataFinal, retorno.getDominioFuncaoProfissional(), retorno.getPucMatricula(), retorno.getPucVinCodigo());
			setarCamposC3(listC3, retorno);
			
			List<RelatorioProdutividadePorAnestesistaConsultaVO> listC4 = new ArrayList<RelatorioProdutividadePorAnestesistaConsultaVO>();
			listC4 = getMbcProfCirurgiasDAO().listarProdutividadeAnestesistaPorCaracteristica(unidadeCirurgica.getSeq(), dataInicial, dataFinal, retorno.getDominioFuncaoProfissional(), retorno.getPucMatricula(), retorno.getPucVinCodigo());
			setarCamposC4(listC4, retorno);
			
			List<RelatorioProdutividadePorAnestesistaConsultaVO> listC5 = new ArrayList<RelatorioProdutividadePorAnestesistaConsultaVO>();
			listC5 = getMbcProfCirurgiasDAO().listarCirurgiasProdutividadeAnestesiaEspecialidade(unidadeCirurgica.getSeq(), dataInicial, dataFinal, retorno.getDominioFuncaoProfissional(), retorno.getPucMatricula(), retorno.getPucVinCodigo());
			setarCamposC5(listC5, retorno);
			
			RelatorioProdutividadePorAnestesistaConsultaVO c6 = new RelatorioProdutividadePorAnestesistaConsultaVO();
			c6 = getMbcProfCirurgiasDAO().listarCirurgiasProdutividadeAnestesiaQuantidade(unidadeCirurgica.getSeq(), dataInicial, dataFinal, retorno.getDominioFuncaoProfissional());
			setarCamposC6(c6, retorno, unidadeCirurgica.getSeq(), dataInicial, dataFinal);
			
			List<RelatorioProdutividadePorAnestesistaConsultaVO> listC7 = new ArrayList<RelatorioProdutividadePorAnestesistaConsultaVO>();
			listC7 = getMbcAnestesiaCirurgiasDAO().listarCirurgiasProdutividadeAnestesiaTipo(unidadeCirurgica.getSeq(), dataInicial, dataFinal, retorno.getDominioFuncaoProfissional(), null, null);
			setarCamposC7(listC7, retorno);
			
			List<RelatorioProdutividadePorAnestesistaConsultaVO> listC8 = new ArrayList<RelatorioProdutividadePorAnestesistaConsultaVO>();
			listC8 = getMbcProfCirurgiasDAO().listarProdutividadeAnestesistaPorCaracteristica(unidadeCirurgica.getSeq(), dataInicial, dataFinal, retorno.getDominioFuncaoProfissional(), null, null);
			setarCamposC8(listC8, retorno);
			
			List<RelatorioProdutividadePorAnestesistaConsultaVO> listC9 = new ArrayList<RelatorioProdutividadePorAnestesistaConsultaVO>();
			listC9 = getMbcProfCirurgiasDAO().listarCirurgiasProdutividadeAnestesiaEspecialidade(unidadeCirurgica.getSeq(), dataInicial, dataFinal, retorno.getDominioFuncaoProfissional(), null, null);
			setarCamposC9(listC9, retorno);
			
			//Média
			retorno.setMediaAtoAnestesico(new BigDecimal(retorno.getQtdAtoAnestesico()).divide(new BigDecimal(retorno.getQtdProfAtoAnestesico()), 5, RoundingMode.HALF_UP));
			retorno.setMediaHoraAnestesia(formatarHora(c6.getQtdHora(), retorno.getQtdProfAtoAnestesico()));
		}
		return listRetorno;
	}
	
	private void setarCamposC9(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC9,
			RelatorioProdutividadePorAnestesistaVO retorno) {
		
		List<LinhaReportVO> listTotalEspecialidadeAnestesia = new ArrayList<LinhaReportVO>();
		for (RelatorioProdutividadePorAnestesistaConsultaVO c9 : listC9) {
			LinhaReportVO especialidade = new LinhaReportVO();
			especialidade.setNumero6(c9.getQuantidade());
			especialidade.setTexto1(c9.getEspSigla() + _HIFEN_ + c9.getNome());
			listTotalEspecialidadeAnestesia.add(especialidade);
		}
		retorno.setTotalEspecialidadeAnestesia(listTotalEspecialidadeAnestesia);
	}
	
	private void setarCamposC8(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC8,
			RelatorioProdutividadePorAnestesistaVO retorno) {
		List<LinhaReportVO> listTotalCaracteristicaAnestesia = new ArrayList<LinhaReportVO>();
		for (RelatorioProdutividadePorAnestesistaConsultaVO c8 : listC8) {
			LinhaReportVO totalCaracteristicaAnestesia = new LinhaReportVO();
			totalCaracteristicaAnestesia.setNumero6(Long.valueOf(c8.getQtdCirurgiaObject().toString()));
			totalCaracteristicaAnestesia.setTexto1(c8.getDescricao());
			listTotalCaracteristicaAnestesia.add(totalCaracteristicaAnestesia);
		}
		retorno.setTotalCaracteristicaAnestesia(listTotalCaracteristicaAnestesia);
	}
	
	private void setarCamposC7(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC7,
			RelatorioProdutividadePorAnestesistaVO retorno) {
		List<LinhaReportVO> listTotalTipoAnestesia = new ArrayList<LinhaReportVO>();
		for (RelatorioProdutividadePorAnestesistaConsultaVO c7 : listC7) {
			LinhaReportVO tipoAnestesia = new LinhaReportVO();
			tipoAnestesia.setTexto1(StringUtils.leftPad(c7.getTamSeq().toString(), 3, "0") + _HIFEN_ + c7.getDescricao());
			tipoAnestesia.setNumero6(c7.getQuantidade());
			listTotalTipoAnestesia.add(tipoAnestesia);
		}
		retorno.setTotalTipoAnestesia(listTotalTipoAnestesia);
	}
	
	private void setarCamposC6(RelatorioProdutividadePorAnestesistaConsultaVO c6, RelatorioProdutividadePorAnestesistaVO retorno, Short unfSeq, Date dataInicial, Date dataFinal) {
		
		c6.setQuantidade(Long.valueOf(c6.getQtdCirurgiaObject().toString()));
		c6.setQtdHora(c6.getQtdHora());
		retorno.setQtdAtoAnestesicoExecutado(obterQtdAtosAnestesicosExecutados(unfSeq, dataInicial, dataFinal, 
				c6.getVinCodigo(), c6.getMatricula(), c6.getQuantidade(), c6.getIndFuncaoProf()));
		retorno.setQtdHoraAnestesia(formatarHora(c6.getQtdHora()));
		retorno.setQtdAtoAnestesico(c6.getQuantidade());
		retorno.setQtdHoraAnestesiaSupervisionado(getMbcProfCirurgiasDAO().obterQtdAtosAnestesicosExecutados(unfSeq, dataInicial, dataFinal, 
				c6.getIndFuncaoProf(), null, null).intValue());
	}

	private void setarCamposC5(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC5,
			RelatorioProdutividadePorAnestesistaVO retorno) {
		
		List<LinhaReportVO> listEspecialidadeAnestesia = new ArrayList<LinhaReportVO>();
		for (RelatorioProdutividadePorAnestesistaConsultaVO c5 : listC5) {
			LinhaReportVO especialidade = new LinhaReportVO();
			especialidade.setNumero6(c5.getQuantidade());
			especialidade.setTexto1(c5.getEspSigla() + _HIFEN_ + c5.getNome());
			listEspecialidadeAnestesia.add(especialidade);
		}
		retorno.setEspecialidadeAnestesia(listEspecialidadeAnestesia);
	}

	private void setarCamposC4(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC4,
			RelatorioProdutividadePorAnestesistaVO retorno) {
		List<LinhaReportVO> listCaracteristicaAnestesia = new ArrayList<LinhaReportVO>();
		for (RelatorioProdutividadePorAnestesistaConsultaVO c4 : listC4) {
			LinhaReportVO tipoAnestesia = new LinhaReportVO();
			tipoAnestesia.setNumero6(Long.valueOf(c4.getQtdCirurgiaObject().toString()));
			tipoAnestesia.setTexto1(c4.getDescricao());
			listCaracteristicaAnestesia.add(tipoAnestesia);
		}
		retorno.setCaracteristicaAnestesia(listCaracteristicaAnestesia);
	}

	private void setarCamposC3(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC3,
			RelatorioProdutividadePorAnestesistaVO retorno) {
		List<LinhaReportVO> listTipoAnestesia = new ArrayList<LinhaReportVO>();
		for (RelatorioProdutividadePorAnestesistaConsultaVO c3 : listC3) {
			LinhaReportVO tipoAnestesia = new LinhaReportVO();
			tipoAnestesia.setTexto1(StringUtils.leftPad(c3.getTamSeq().toString(), 3, "0") + _HIFEN_ + c3.getDescricao());
			tipoAnestesia.setNumero6(c3.getQuantidade());
			listTipoAnestesia.add(tipoAnestesia);
		}
		retorno.setTipoAnestesia(listTipoAnestesia);
	}

	private void setarCamposC2(Short unfSeq, Date dataInicial, Date dataFinal, List<RelatorioProdutividadePorAnestesistaConsultaVO> listC2,
			List<RelatorioProdutividadePorAnestesistaVO> listRetorno) {
		for (RelatorioProdutividadePorAnestesistaConsultaVO c2 : listC2) {
			RelatorioProdutividadePorAnestesistaVO retorno = new RelatorioProdutividadePorAnestesistaVO();
			retorno.setPucMatricula(c2.getMatricula());
			retorno.setPucVinCodigo(c2.getVinCodigo());
			retorno.setDescFuncaoProfissional(c2.getIndFuncaoProf().getCodigo() + _HIFEN_ + c2.getIndFuncaoProf().getDescricao().toUpperCase());
			retorno.setDescSerCodigoMatricula(formatarVinCodigoMatricula(c2.getVinCodigo(),c2.getMatricula())); 
			retorno.setNomeFuncionario(c2.getNome());
			retorno.setQtdCirurgia(c2.getQuantidade());
			retorno.setQtdExecutado(obterQtdAtosAnestesicosExecutados(c2.getPucUnfSeq(), c2.getDataInicioAnestesia(), c2.getDataFimAnestesia(), 
					c2.getVinCodigo(), c2.getMatricula(), c2.getQuantidade(), c2.getIndFuncaoProf()));
			retorno.setQtdSupervisionado(getMbcProfCirurgiasDAO().obterQtdAtosAnestesicosExecutados(unfSeq, dataInicial, dataFinal, 
					c2.getIndFuncaoProf(), c2.getVinCodigo(), c2.getMatricula()).intValue());
			retorno.setQtdHora(formatarHora(c2.getQtdHora()));
			retorno.setQtdProfAtoAnestesico(calcularQtdAnestesistaPorGrupo(listC2, c2.getIndFuncaoProf()));
			retorno.setDominioFuncaoProfissional(c2.getIndFuncaoProf());
			listRetorno.add(retorno);
		}
	}
 
	private Integer calcularQtdAnestesistaPorGrupo(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC2,
			DominioFuncaoProfissional indFuncaoProf) {
		Integer retorno = 0;
		for (RelatorioProdutividadePorAnestesistaConsultaVO c2 : listC2) {
			if(c2.getIndFuncaoProf().equals(indFuncaoProf)){
				retorno ++;
			}
		}
		return retorno;
	}

	//RN3 RN7
	private String formatarHora(Long qtdHora) {
		String retorno;
		if(qtdHora != null){
			retorno = (qtdHora/60) + "H" + (qtdHora%60) + "M";
		}else{
			retorno = "HM";
		}
		return retorno;
	}
	
	private String formatarHora(Long qtdHora, Integer divisor) {
		if(qtdHora == null){
			return "";
		}
		qtdHora = qtdHora/divisor;
		return (qtdHora/60) + "H" + (qtdHora%60) + "M";
	}

	private List<DominioFuncaoProfissional> preencherListDominioFuncaoProfissional(
			DominioFuncaoProfissional funcaoProfissional) {
		List<DominioFuncaoProfissional> listDominioFuncaoProfissional = new ArrayList<DominioFuncaoProfissional>();
		if(funcaoProfissional == null){
			listDominioFuncaoProfissional = Arrays.asList(
					DominioFuncaoProfissional.ANP,
					DominioFuncaoProfissional.ANC,
					DominioFuncaoProfissional.ANR);
		}else{
			listDominioFuncaoProfissional.add(funcaoProfissional);
		} 		
		return listDominioFuncaoProfissional;
	}
	
	//RN1 RN5
	private Long obterQtdAtosAnestesicosExecutados(Short pucUnfSeq,
			Date dataInicioAnestesia, Date dataFimAnestesia,
			Short vinCodigo, Integer matricula, Long qtdCirurgia, DominioFuncaoProfissional indFuncaoProfissional) {
		Long atosAnestesicosExecutados;
		//RN2 RN4 RN6 RN8
		Long rQtde = getMbcProfCirurgiasDAO().obterQtdAtosAnestesicosExecutados(pucUnfSeq, dataInicioAnestesia, dataFimAnestesia, 
				indFuncaoProfissional, vinCodigo, matricula);
	
		if(DominioFuncaoProfissional.ANR.equals(indFuncaoProfissional)){
			atosAnestesicosExecutados = 0L;
		}else{
			atosAnestesicosExecutados = (qtdCirurgia != null ? qtdCirurgia : 0L) - (rQtde != null ? rQtde : 0L);
		}
		
		return atosAnestesicosExecutados;
	}

	private String formatarVinCodigoMatricula(Short vinCodigo, Integer matricula) {
		String retorno;
		
		retorno = StringUtils.leftPad(matricula.toString(), 6, "0");
		retorno = vinCodigo + "." + retorno.substring(0,5) + "." + retorno.substring(5);
		
		return retorno;
		
	}

	private void tratarC2(List<RelatorioProdutividadePorAnestesistaConsultaVO> listC2, Date dataInicial, Date dataFinal) {
		for (RelatorioProdutividadePorAnestesistaConsultaVO c2 : listC2) {
			c2.setQuantidade(Long.valueOf(c2.getQtdCirurgiaObject().toString()));
			c2.setDataInicioAnestesia(dataInicial);
			c2.setDataFimAnestesia(dataFinal);
		}
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
}
