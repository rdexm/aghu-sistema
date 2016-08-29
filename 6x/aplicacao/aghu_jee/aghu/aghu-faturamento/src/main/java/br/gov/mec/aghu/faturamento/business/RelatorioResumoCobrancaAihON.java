package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoModalidadeDAO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoEncerramentoPreviaVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihServicosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioResumoCobrancaAihON extends BaseBusiness implements
		Serializable {


@EJB
private RelatorioResumoCobrancaAihRN relatorioResumoCobrancaAihRN;

	private static final Log LOG = LogFactory
			.getLog(RelatorioResumoCobrancaAihON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

	@Inject
	private FatProcedimentoModalidadeDAO fatProcedimentoModalidadeDAO;
	
	private static final long serialVersionUID = -3764000473585553593L;

	/*
	 * * ORADB: FATC_BUSCA_INSTR_REG
	 */
	public Boolean buscaInstrRegistro(final Integer iphSeq,
			final Short iphPhoSeq, final String codRegistro) {
		return getRelatorioResumoCobrancaAihRN().buscaInstrRegistro(iphSeq,
				iphPhoSeq, codRegistro);
	}

	/*
	 * * ORADB: FATC_BUSCA_PROCED_PR_CTA
	 */
	public String buscaProcedimentoPrConta(final Integer seq,
			final Short phoSeq, final Integer eaiCthSeq, final Long codTabela) {
		return getRelatorioResumoCobrancaAihRN().buscaProcedimentoPrConta(seq,
				phoSeq, eaiCthSeq, codTabela);
	}

	protected RelatorioResumoCobrancaAihRN getRelatorioResumoCobrancaAihRN() {
		return relatorioResumoCobrancaAihRN;
	}

	public List<ResumoCobrancaAihServicosVO> listarAtosMedicos(Integer seq, Integer cthSeq) {

		List<ResumoCobrancaAihServicosVO> listaVO = getFatItensProcedHospitalarDAO().listarAtosMedicos(seq, cthSeq);

		// ITERAR LISTA
		for (ResumoCobrancaAihServicosVO vo : listaVO) {
			populaVOPart1(vo);
			populaVOPart2(vo);
		}

		return listaVO;
	}

	private void populaVOPart1(ResumoCobrancaAihServicosVO vo) {
		String registro;
		String registroOrder;
		String complexidade;
		String financ;

		if (this.buscaInstrRegistro(vo.getIphseq(), vo.getIphphoseq(), "03")) {
			registro = "1PRINCIPAL";
		} else {
			registro = null;
		}

		if (this.buscaInstrRegistro(vo.getIphseq(), vo.getIphphoseq(), "03")) {
			registroOrder = "1";
		} else if (this.buscaInstrRegistro(vo.getIphseq(), vo.getIphphoseq(), "04")) {
			registroOrder = "2";
		} else {
			registroOrder = "3";
		}

		if (Integer.valueOf(2).equals(vo.getFcfseq())) {
			financ = "1FAEC";
		} else if (Integer.valueOf(4).equals(vo.getFcfseq())) {
			financ = "2ALTA";
		} else if (Integer.valueOf(3).equals(vo.getFcfseq())) {
			financ = "3MEDIA";
		} else {
			financ = "4XXXXX";
		}

		if (Integer.valueOf(4).equals(vo.getFcfseq())) {
			complexidade = "ALTA";
		} else if (Integer.valueOf(5).equals(vo.getFcfseq()) || Integer.valueOf(6).equals(vo.getFcfseq()) || Integer.valueOf(7).equals(vo.getFcfseq())
				|| Integer.valueOf(8).equals(vo.getFcfseq())) {
			complexidade = "MEDIA";
		} else {
			complexidade = "XXXXXX";
		}

		vo.setFinanc(financ);
		vo.setRegistro(registro);
		vo.setRegistroorder(registroOrder);
		vo.setComplexidade(complexidade);		
	}	
	
	private void populaVOPart2(ResumoCobrancaAihServicosVO vo) {
		BigDecimal valorPrincipal = BigDecimal.ZERO;
		String codigoPad;

		if (vo.getValvalorsh() != null) {
			valorPrincipal = valorPrincipal.add(vo.getValvalorsh());
		}
		if (vo.getValvalorsp() != null) {
			valorPrincipal = valorPrincipal.add(vo.getValvalorsp());
		}

		if (vo.getValvalorprocedimento() != null) {
			valorPrincipal = valorPrincipal.add(vo.getValvalorprocedimento());
		}

		if (vo.getValvalorsadt() != null) {
			valorPrincipal = valorPrincipal.add(vo.getValvalorsadt());
		}

		if (vo.getCodigo() != null) {
			codigoPad = vo.getCodigo().toString();
			codigoPad = StringUtils.leftPad(codigoPad, 10, "0");
			if (codigoPad.substring(0, 2).equals("07")) {
				vo.setCodigopad("00");
			} else {
				vo.setCodigopad(codigoPad.substring(0, 2));
			}
		}

		vo.setValorprincipal(valorPrincipal);
		vo.setSequencia(this.buscaProcedimentoPrConta(vo.getIphseq(), vo.getIphphoseq(), vo.getEaicthseq(), vo.getIphcodsus()));
		
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}
	
	/**
	 * @ORADB fatc_busca_modalidade
	 * #36463
	 */
	public Short aghcModuloOnzeCompl(FatEspelhoEncerramentoPreviaVO objetoVO){
		
		String val = obterObjetoConcatenado(objetoVO);
		
		Short a1  = (short) (obterValorSubString(val, 0, 1) * 5);
		Short a2  = (short) (obterValorSubString(val, 1, 1) * 4);
		Short a3  = (short) (obterValorSubString(val, 2, 1) * 3);
		Short a4  = (short) (obterValorSubString(val, 3, 1) * 2);
		Short a5  = (short) (obterValorSubString(val, 4, 1) * 9);
		Short a6  = (short) (obterValorSubString(val, 5, 1) * 8);
		Short a7  = (short) (obterValorSubString(val, 6, 1) * 7);
		Short a8  = (short) (obterValorSubString(val, 7, 1) * 6);
		Short a9  = (short) (obterValorSubString(val, 8, 1) * 5);
		Short a10 = (short) (obterValorSubString(val, 9, 1) * 4);
		Short a11 = (short) (obterValorSubString(val, 10, 1) * 3);
		Short a12 = (short) (obterValorSubString(val, 11, 1) * 2);
		
		Integer atot = (a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12);
		Integer num = atot % 11;
		
		if(num > 1){
			return (short) (11 - num);
		}else{
			return 0;
		}
	}
	
	/**
	 * Método que retorno valor "" caso posição é igual a 0
	 * @param val
	 * @param posicao
	 * @param qtdCampos
	 * @return
	 */
	public Short obterValorSubString(String val, int posicao, int qtdCampos){
		
		String retorno = null;
		retorno = val.substring(posicao, posicao + qtdCampos);
		
		if(!retorno.equals("")){
			return Short.parseShort(retorno);
		}else{
			return (short) 0;
		}
	}
	
	public String obterObjetoConcatenado(FatEspelhoEncerramentoPreviaVO objeto){
		
		String parteInicio = objeto.getDciCpeAno().toString().concat("02");
		String parteFim;
		
		if(objeto.getCthSeq() != null){
			
			parteFim = objeto.getCthSeq().toString();
			
			for(int i = objeto.getCthSeq().toString().length(); i < 6; i++){
				
				parteFim = "0".concat(parteFim);
			}
		}else {
			parteFim = "0";
			parteFim = parteFim.concat("00000");
		}
		
		return parteInicio.concat(parteFim);	
	}
	
	/**
	 * @ORADB fatc_busca_modalidade
	 * #36463
	 */
	public Short fatcBuscaModalidade(Integer p_iph_seq, Short p_pho_seq, Date p_data_int, Date p_data_alta){
		
		Integer vQtd = 0;
		
		List<Short> listCRegistro = fatProcedimentoModalidadeDAO.obterListaMotCodigo(p_iph_seq, p_pho_seq);
		
		vQtd = listCRegistro.size();
		
		if(vQtd == 0){
			return 0;
		}else if(vQtd == 1){
			return listCRegistro.get(0);
		}else if(DateUtil.isDatasIguais(DateUtil.truncaData(p_data_int), DateUtil.truncaData(p_data_alta))){
			return 3;
		}else{
			return 2;
		}
	}
}
