package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioGuiaAtendimentoUnimedSubProced1VO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioGuiaAtendimentoUnimedSubProced2VO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.dao.ProcEfetDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.ProcEfet;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ON do Relatório #43088 Imprimir Guia de atendimento da Unimed
 * 
 * @author aghu
 *
 */
@Stateless
public class ImprimirGuiaAtendimentoUnimedON extends BaseBusiness {

	private static final long serialVersionUID = 1963919264175300735L;

	private static final Log LOG = LogFactory.getLog(ImprimirGuiaAtendimentoUnimedON.class);

	@EJB
	private ImprimirGuiaAtendimentoUnimedRN imprimirGuiaAtendimentoUnimedRN;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject 
	private ProcEfetDAO procEfetDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public RelatorioGuiaAtendimentoUnimedVO montarGuiaATendimentoUnimed(List<ConsultaGuiaAtendimentoUnimedVO> listaGuiaAtdUnimed, Integer conNumero) throws ApplicationBusinessException {
		RelatorioGuiaAtendimentoUnimedVO retorno = new RelatorioGuiaAtendimentoUnimedVO();
		
		AghParametros paramUFSigla = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HOSPITAL_UF_SIGLA);
		
		FatConvenioSaude fatConvenioSaude = this.faturamentoFacade.obterFatConvenioSaudePorId((short) 99);
	
		// conv.cod_ans,
		retorno.setCodAns(listaGuiaAtdUnimed.get(0).getCodAns() != null ? listaGuiaAtdUnimed.get(0).getCodAns().toString() : null);
		
		// a.numero_guia nro_guia,
		retorno.setGuia(listaGuiaAtdUnimed.get(0).getNumeroGuia() != null ? listaGuiaAtdUnimed.get(0).getNumeroGuia().toString() : null);
		
		// a.data data_emis,
		retorno.setDataEmis(listaGuiaAtdUnimed.get(0).getData() != null ? 
				DateUtil.obterDataFormatada(listaGuiaAtdUnimed.get(0).getData(), "dd/MM/yyyy").toString() : null);
		
		// rapc_vesetem_unimed(con.ser_matricula_consultado, con.ser_vin_codigo_consultado,null,pac.prontuario,a.data) matr,
		retorno.setMatr(this.imprimirGuiaAtendimentoUnimedRN.rapcVesetemUnimed(listaGuiaAtdUnimed.get(0).getMatriculaConsultado(), 
				listaGuiaAtdUnimed.get(0).getVinCodigoConsultado(), null, listaGuiaAtdUnimed.get(0).getProntuario(), 
				listaGuiaAtdUnimed.get(0).getData()));
		
		// pac.nome nome_pac,
		retorno.setNomePac(listaGuiaAtdUnimed.get(0).getNomePaciente());
		
		// aipc_get_cartao_sus(intd.cod_prnt) cartao_sus,
		retorno.setCartaoSus(this.imprimirGuiaAtendimentoUnimedRN.obterCartaoSus(listaGuiaAtdUnimed.get(0).getCodPrnt()).toString());
		
		String ffcFMatriculaProf = this.imprimirGuiaAtendimentoUnimedRN.ffcFMatriculaProf(listaGuiaAtdUnimed.get(0).getAtdSeq(), 
				listaGuiaAtdUnimed.get(0).getMexSeq(), listaGuiaAtdUnimed.get(0).getCspCnvCodigo());
		
		// to_number(substr(ffc_f_matricula_prof(cta.atd_seq,cta.mex_seq,cta.csp_cnv_codigo),4,11)) codigo_operadora,
		Long codOperadora = Long.valueOf(StringUtils.substring(ffcFMatriculaProf, 3, 14));
		retorno.setCodigoOperadora(String.valueOf(codOperadora));
				
		// rtrim(substr(ffc_f_matricula_prof(cta.atd_seq,cta.mex_seq,cta.csp_cnv_codigo),15,100),' ') nome_prof,
		retorno.setNomeProf(StringUtils.trim(StringUtils.substring(ffcFMatriculaProf, 14, 114)));
				
		// substr(ffc_f_matricula_prof(cta.atd_seq,cta.mex_seq,cta.csp_cnv_codigo),115,3) conselho,
		retorno.setConselho(StringUtils.substring(ffcFMatriculaProf, 114, 117));
			 	
		// to_number(substr(ffc_f_matricula_prof(cta.atd_seq,cta.mex_seq, cta.csp_cnv_codigo),118,10)) nro_conselho,
		Long nroConselho = Long.valueOf(StringUtils.trim(StringUtils.substring(ffcFMatriculaProf, 117, 127)));
		retorno.setNroConselho(String.valueOf(nroConselho));
				
		// 'RS' uf, -- Parâmetro PS5
		retorno.setUf(paramUFSigla.getVlrTexto());		
		
		this.montarGuiaATendimentoUnimedParte2(listaGuiaAtdUnimed, retorno, fatConvenioSaude);
		
		this.montarGuiaATendimentoUnimedParte3(listaGuiaAtdUnimed, retorno, fatConvenioSaude);
		
		this.montarGuiaATendimentoUnimedParte4(listaGuiaAtdUnimed, retorno, conNumero);
		
		this.montarProcedimentos(listaGuiaAtdUnimed, retorno);
		
		return retorno;
	}
	
	private void montarGuiaATendimentoUnimedParte2(List<ConsultaGuiaAtendimentoUnimedVO> listaGuiaAtdUnimed,
			RelatorioGuiaAtendimentoUnimedVO retorno, FatConvenioSaude fatConvenioSaude) throws ApplicationBusinessException {
		
		AghParametros paramNomeDoHospital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);	
		
		// ffc_f_cod_operadora (cta.csp_cnv_codigo, cta.csp_seq, cta.nro) operadora,
		Integer codOperadora = this.imprimirGuiaAtendimentoUnimedRN.ffcFCodOperadora(listaGuiaAtdUnimed.get(0).getCspCnvCodigo(), 
				listaGuiaAtdUnimed.get(0).getCspSeq(), listaGuiaAtdUnimed.get(0).getNro());
		retorno.setOperadora(codOperadora != Integer.valueOf(0) ? codOperadora.toString() : null);
		
		// 'Hospital de Clínicas de Porto Alegre' contratado, -- Parâmetro PS4
		retorno.setContratado(paramNomeDoHospital.getVlrTexto());
				
		// decode(nvl(cnvop.logradouro,' '),' ',' ','081' ) tl_exec,
		retorno.setTlExec(fatConvenioSaude.getLogradouro() == null ? null : "081");
		
		// decode(nvl(cnvop.logradouro,' '),' ',' ', cnvop.logradouro ||' - ' ||cnvop.nro_logradouro ||' - ' ||cnvop.compl_logradouro) logr_exec,
		retorno.setLogrExec(fatConvenioSaude.getLogradouro() == null ? "" : (fatConvenioSaude.getLogradouro()
					.concat(" - ").concat(fatConvenioSaude.getNumeroLogradouro() == null ? "" : fatConvenioSaude.getNumeroLogradouro().toString())
					.concat(" - ").concat(fatConvenioSaude.getComplementoLogradouro() == null ? "" :fatConvenioSaude.getComplementoLogradouro())));
	}
	
	private void montarGuiaATendimentoUnimedParte3(List<ConsultaGuiaAtendimentoUnimedVO> listaGuiaAtdUnimed,
			RelatorioGuiaAtendimentoUnimedVO retorno, FatConvenioSaude fatConvenioSaude) throws ApplicationBusinessException {
						
		// decode(nvl(cnvop.logradouro,' '),' ',' ','RS' ) uf_exec,
		retorno.setUfExec(fatConvenioSaude.getLogradouro() == null ? null : "RS");
		
		// decode(nvl(cnvop.cidade,' '),' ',' ',rapc_cod_ibge(cnvop.cidade)) cod_ibge_exec,
		Integer codIbge = this.imprimirGuiaAtendimentoUnimedRN.obterCodigoIbgePorNomeCidade(fatConvenioSaude.getCidade());
		retorno.setCodIbgeExec(fatConvenioSaude.getCidade() == null ? null : codIbge.toString());
		
		// cnvop.cidade cidade_exec,
		retorno.setCidadeExec(fatConvenioSaude.getCidade());
						
		// cnvop.cep cep_exec,
		retorno.setCepExec(fatConvenioSaude.getCep().toString());
		
		// cnoper.cod_ans cod_cnes_exec,
		retorno.setCodCnesExec(listaGuiaAtdUnimed.get(0).getCodCnesExec() != null ? listaGuiaAtdUnimed.get(0).getCodCnesExec().toString() : null);
					
		// 4 tipo_atend ,
		retorno.setTipoAtend("4");
						
		// 5 saida ,
		retorno.setSaida("5");		
						
		// ffc_f_busca_senha(cta.nro) senha,
		retorno.setSenha(this.imprimirGuiaAtendimentoUnimedRN.ffcFBuscaSenha(listaGuiaAtdUnimed.get(0).getNro()));
						
		// to_date(ffc_f_busca_data_senha(cta.nro)) data_aut,
		retorno.setDataAut(this.imprimirGuiaAtendimentoUnimedRN.ffcFBuscaDataSenha(listaGuiaAtdUnimed.get(0).getNro()));
						
		// replace(plano.descricao,'PLANO ') plano,
		retorno.setPlano(StringUtils.replace(listaGuiaAtdUnimed.get(0).getDescricaoPlano(), "PLANO", ""));
	}
	
	private void montarGuiaATendimentoUnimedParte4(List<ConsultaGuiaAtendimentoUnimedVO> listaGuiaAtdUnimed,
			RelatorioGuiaAtendimentoUnimedVO retorno, Integer conNumero) {
		
		//C3
		List<ProcEfet> procEfet = this.procEfetDAO.listarProcEfetPorConNumero(conNumero);
		if (procEfet != null && !procEfet.isEmpty()) {
			retorno.setQtdeCsh(procEfet.get(0).getQtdeCsh() == null ? null : formatarNumeroMoeda(Double.valueOf(procEfet.get(0).getQtdeCsh().toString())));
			retorno.setGuia(procEfet.get(0).getNumeroGuia() == null ? null : procEfet.get(0).getNumeroGuia().toString());
			
			if (procEfet.get(0).getCntaConv() != null && procEfet.get(0).getCntaConv().getConv() != null) {
				retorno.setConvenio(procEfet.get(0).getCntaConv().getConv().getDescr());
			}
		}		
	}
	
	public RelatorioGuiaAtendimentoUnimedVO montarProcedimentos(List<ConsultaGuiaAtendimentoUnimedVO> listaGuiaAtdUnimed,
			RelatorioGuiaAtendimentoUnimedVO retorno) {
		
		BigDecimal somaVlrTotal = BigDecimal.ZERO;
		
		RelatorioGuiaAtendimentoUnimedSubProced1VO subRelataorio1 = new RelatorioGuiaAtendimentoUnimedSubProced1VO();
		// decode(intd.sgla_esp,'EME','U','E') carater,
		subRelataorio1.setCarater("EME".equals(listaGuiaAtdUnimed.get(0).getSiglaEsp()) ? "U" : "E");
		subRelataorio1.setTabela(listaGuiaAtdUnimed.get(0).getTabela());
		subRelataorio1.setCodProcedimento(listaGuiaAtdUnimed.get(0).getCodTabPgto().toString());
		subRelataorio1.setDescrProcedimento(listaGuiaAtdUnimed.get(0).getDscrTabPgto());
		subRelataorio1.setQtde(listaGuiaAtdUnimed.get(0).getQtde().toString());
		
		retorno.getSubRelatorioProcedimentos1().add(subRelataorio1);
		
		for (ConsultaGuiaAtendimentoUnimedVO consultaGuiaAtendimentoUnimedVO : listaGuiaAtdUnimed) {
			
			RelatorioGuiaAtendimentoUnimedSubProced2VO subRelataorio2 = new RelatorioGuiaAtendimentoUnimedSubProced2VO();
			
			
			// decode(substr(tptab.dscr,1,10),'GEAP CBHPM','06', decode(substr(tptab.dscr,1,			
			subRelataorio2.setTabela(consultaGuiaAtendimentoUnimedVO.getTabela());
			
			// b.cod cod_procedimento,
			subRelataorio2.setCodProcedimento(consultaGuiaAtendimentoUnimedVO.getCodTabPgto().toString());
			
			// b.dscr descr_procedimento,
			subRelataorio2.setDescrProcedimento(consultaGuiaAtendimentoUnimedVO.getDscrTabPgto());
			
			// a.qtde,			
			subRelataorio2.setQtde(consultaGuiaAtendimentoUnimedVO.getQtde().toString());
			
			// a.data dt_proc,
			subRelataorio2.setDtProc(consultaGuiaAtendimentoUnimedVO.getData() != null ? 
					DateUtil.obterDataFormatada(consultaGuiaAtendimentoUnimedVO.getData(), "dd/MM/yyyy").toString() : null);
			
			// decode(tptab.tipo_tab,'P', decode(nvl(a.vias_accs,0),0,'U',31,'M',32,'D',33,'D','U'),null) via,
			if ("P".equals(consultaGuiaAtendimentoUnimedVO.getTipoTab())) {
				if (consultaGuiaAtendimentoUnimedVO.getViasAccs() == 31) {
					subRelataorio2.setVia("M");
				} else if (consultaGuiaAtendimentoUnimedVO.getViasAccs() == 32 || consultaGuiaAtendimentoUnimedVO.getViasAccs() == 33) {
					subRelataorio2.setVia("D");
				} else {
					subRelataorio2.setVia("U");
				}
			} else {
				subRelataorio2.setVia(null);
			}
			
			// decode(nvl(a.ind_discrimina_desconto,'S'),'N', decode(nvl(a.valor_desconto,0),0,0, 
			//	round(a.valor_desconto*100/(a.qtde_csh + a.valor_desconto))) ,0) percentual,
			
			if ("N".equals(consultaGuiaAtendimentoUnimedVO.getIndDiscriminaDesconto())) {
				
				if (consultaGuiaAtendimentoUnimedVO.getValorDesconto() != null &&
						consultaGuiaAtendimentoUnimedVO.getValorDesconto() != 0) {
					
					BigDecimal vlrDesconto = new BigDecimal(consultaGuiaAtendimentoUnimedVO.getValorDesconto());
					BigDecimal desconto = BigDecimal.ZERO;

					desconto = vlrDesconto.multiply(new BigDecimal(100))
							.divide(consultaGuiaAtendimentoUnimedVO.getQtdeCsh().add(vlrDesconto), RoundingMode.HALF_UP);
					subRelataorio2.setPercentual(desconto.toString());
					
				}else {
					subRelataorio2.setPercentual("");
				}
			} else {
				subRelataorio2.setPercentual("");
			}	
			
			somaVlrTotal = this.calcularValorUnitarioETotal(consultaGuiaAtendimentoUnimedVO, subRelataorio2, somaVlrTotal);
			
			retorno.getSubRelatorioProcedimentos2().add(subRelataorio2);			
			
			retorno.setSomaVlrTot(formatarNumeroMoeda(Double.valueOf(somaVlrTotal.toString())));
		}
		
		return retorno;
	}

	private BigDecimal calcularValorUnitarioETotal(ConsultaGuiaAtendimentoUnimedVO consultaGuiaAtendimentoUnimedVO,
			RelatorioGuiaAtendimentoUnimedSubProced2VO subRelataorio2, BigDecimal somaVlrTotal) {
		
		// round( decode(nvl(y.qtde_ch_mat,0),0,a.qtde_csh,a.qtde_csh - (a.qtde_csh * ((
		//		  y.qtde_ch_mat ) / ((y.qtde_csh + b.qtde_csh_prof ) * h.valor + y.qtde_ch_mat
		//		  + (nvl(b.qtde_m2,0) * nvl(flm.valor,0) ))))),2) vlr_tot,
		
		// round( (decode(nvl(y.qtde_ch_mat,0),0,a.qtde_csh,a.qtde_csh - (a.qtde_csh * (
		//		  (y.qtde_ch_mat ) / ((y.qtde_csh + b.qtde_csh_prof ) * h.valor + y.qtde_ch_mat
		//		  + (nvl(b.qtde_m2,0) * nvl(flm.valor,0))))))/ a.qtde),2) vlr_unit,		
			
		
		BigDecimal vlrTotal = BigDecimal.ZERO;
		BigDecimal aux = BigDecimal.ZERO;
		BigDecimal qtdeCshTabPgtoComp = consultaGuiaAtendimentoUnimedVO.getQtdeCshTabPgtoComp();
		BigDecimal qtdeCshProf = consultaGuiaAtendimentoUnimedVO.getQtdeCshProf();
		BigDecimal hValorConvPlano = consultaGuiaAtendimentoUnimedVO.gethValorConvPlano();
		BigDecimal qtdeChMatTabPgtoComp = consultaGuiaAtendimentoUnimedVO.getQtdeChMatTabPgtoComp();
		BigDecimal qtdeM2 = consultaGuiaAtendimentoUnimedVO.getQtdeM2() == null ? BigDecimal.ZERO : consultaGuiaAtendimentoUnimedVO.getQtdeM2();
		BigDecimal flmValorConvPlano = consultaGuiaAtendimentoUnimedVO.getFlmValorConvPlano() == null ? BigDecimal.ZERO : consultaGuiaAtendimentoUnimedVO.getFlmValorConvPlano();
		BigDecimal qtdeCsh = consultaGuiaAtendimentoUnimedVO.getQtdeCsh();
		
		if (consultaGuiaAtendimentoUnimedVO.getQtdeChMatTabPgtoComp() != null &&
				consultaGuiaAtendimentoUnimedVO.getQtdeChMatTabPgtoComp() != BigDecimal.ZERO) {			
			
			aux = (qtdeCshTabPgtoComp.add(qtdeCshProf)).multiply(hValorConvPlano).add(qtdeChMatTabPgtoComp)
					.add(qtdeM2.multiply(flmValorConvPlano));
			
			vlrTotal = qtdeCsh.subtract(qtdeCsh.multiply(qtdeChMatTabPgtoComp.divide(aux)));
			
		} else {
			vlrTotal = consultaGuiaAtendimentoUnimedVO.getQtdeCsh();
		}		
		subRelataorio2.setVlrTot(formatarNumeroMoeda(Double.valueOf(vlrTotal.round(new MathContext(2)).toString())));
		subRelataorio2.setVlrUnit(formatarNumeroMoeda(Double.valueOf(vlrTotal.divide(consultaGuiaAtendimentoUnimedVO.getQtde()).round(new MathContext(2)).toString())));
		
		somaVlrTotal = somaVlrTotal.add(vlrTotal.round(new MathContext(2)));	
		
		return somaVlrTotal;
	}

	private String formatarNumeroMoeda(Double valor) {
		if (valor == null) {
			return "";
		} else {
			Locale loc = new Locale("pt", "BR");
			NumberFormat nb = NumberFormat.getInstance(loc);
			nb.setMinimumFractionDigits(2);
			nb.setMaximumFractionDigits(2);

			return nb.format(valor); 
		}
	}
}
