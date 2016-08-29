package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.GeracaoArquivoFaturamentoCompetenciaInternacaoRN.DominioTipoArquivoDcih;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.vo.ArquivoSUSVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosMedVO;
import br.gov.mec.aghu.faturamento.vo.CursorCEAIVO;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class GeracaoArquivoFaturamentoCompetenciaInternacaoRN1 
extends
	AbstractFatDebugExtraFileLogEnable {
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;

	@EJB
	private LayoutArquivoSusRN layoutArquivoSusRN;
	
	@EJB
	private BuscaCpfCboResponsavelPorIchRN buscaCpfCboResponsavelPorIchRN;
	
	@EJB
	private FatcBuscaServClassRN fatcBuscaServClassRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4594889926138542711L;


	BigDecimal tetoSus = null;
	Short fogSgrGrpSeq = null;
	
	// Busca código CNES ETB 260107
	BigInteger vCodCNES = null;
	
	final int[] rangeVIndices = {10,19,28,37,46,55,64,73,82,91,100,109,118,127,136,145,154,163,172,181,190,199,208,217,226,235,244,253,262,271}; 
	DominioTipoArquivoDcih vArquivoDCIH = null;
	DominioTipoArquivoDcih vArquivoDCIHAnt = null;

	String vCapacAnt = null;
	String vArquivo = null;
	BigDecimal valorTotal = BigDecimal.ZERO;
	StringBuilder vLinhaAtm = new StringBuilder();
	String vLinhaArq = null;
	
	int seqComplArqSus = 0;

	List<CursorCEAIVO> ceais= null;
	Short modalidade = null;
	int vPrimeira = 0;

	int vIndice = 0;
	
	ArquivoSUSVO arq = null;

	List<ArquivoSUSVO> arqs = null;
	
	List<CursorBuscaContaVO> cBuscaConta = null;
	
	protected void inicializar() throws ApplicationBusinessException {
		
		tetoSus = new BigDecimal(buscarVlrTextoAghParametro(AghuParametrosEnum.P_VALOR_MAXIMO_SUS));
		fogSgrGrpSeq = buscarVlrShortAghParametro(AghuParametrosEnum.P_GRUPO_OPM);
		
		// Busca código CNES ETB 260107
		vCodCNES = obterCnesHospital(this.getAghuFacade());
		
		arq = new ArquivoSUSVO();
		
		arqs = new ArrayList<ArquivoSUSVO>();
	}
	
	protected void processaAtosMedicos(CursorCEAIVO cursorCEAIVO, CursorCAtosMedVO rAtosMed, LayoutArquivoSusVO laSusVO) throws ApplicationBusinessException {
		
    	rAtosMed.setCbo(processarFatcBuscaCbo(cursorCEAIVO.getCthSeq(), rAtosMed,  getBuscaCpfCboResponsavelPorIchRN()));

    	// FATC_BUSCA_serv_class(P_CTH_SEQ,aam.iph_seq,aam.iph_pho_seq)
    	String servClass = getFatcBuscaServClassRN().fatcBuscaServClass( rAtosMed.getEaiCthSeq(), 
    																  	 rAtosMed.getAmaIphSeq(), 
    																  	 rAtosMed.getAmaIphPhoSeq(),
    																  	 null
    															       );
    	rAtosMed.setServClass(servClass);
		final LayoutArquivoSusVO laSusVORAtosMed = getLayoutArquivoSusRN().aplicarLayout(rAtosMed);
		
		// ETB 22/09/2008
		vIndice++;
		
		if (ArrayUtils.contains(rangeVIndices, vIndice)) {
			
			// v_linha_atm := rpad(NVL(v_linha_atm,'0'),730,'0');
			if(vLinhaAtm == null){
				vLinhaAtm = new StringBuilder();
				vLinhaAtm.append(StringUtils.rightPad(String.valueOf(0), 730));
				
			} else if(vLinhaAtm.length() < 730) {
				String aux = StringUtils.rightPad(vLinhaAtm.toString(), 730, '0');
				vLinhaAtm = new StringBuilder();
				vLinhaAtm.append(aux);
			}
			
			vLinhaArq = laSusVO.getvParte10() + 
						laSusVO.getvParte11() + 
						laSusVO.getvParte12() + 
						laSusVO.getvParte1()  +
						vLinhaAtm.toString()  +
						laSusVO.getvParte3();
			
			// text_io.put_line(sus_arq,v_linha_arq);
			arq.addLinha(vLinhaArq);
			vLinhaAtm = new StringBuilder();
			
			if(vIndice > 9){
				// marina 06/09/2011
				laSusVO.setvParte11("03");
			}
		} // Fim ETB 22/09/2008
		
		// v_linha_atm := v_linha_atm||R_ATOS_MED.ATOS_MED;
		vLinhaAtm.append(laSusVORAtosMed.getAtosMed()); 
	}
	
	protected BigInteger obterCnesHospital(final IAghuFacade aghuFacade) throws ApplicationBusinessException {

		//aghp_get_parametro('P_CNES_HCPA','FATF_ATU_COMP_INT','N','N' ,v_vlr_data,v_cod_cnes,v_vlr_texto,v_msg);
		final List<AghInstituicoesHospitalares> listaInst = aghuFacade.listarInstHospPorIndLocalAtivo();
		
		if ((listaInst == null) || listaInst.isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ARQ_SUS_CNES_LOCAL_NAO_ENCONTRADO);
		}
		if (listaInst.size() > 1) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.ARQ_SUS_CNES_LOCAL_NAO_UNICO);
		}
		
		return BigInteger.valueOf(listaInst.get(0).getCnes().longValue());
	}

	protected String processarFatcBuscaCbo(Integer cthSeq, CursorCAtosMedVO rAtosMed, BuscaCpfCboResponsavelPorIchRN buscaCpfCboResponsavelPorIchRN) throws ApplicationBusinessException {
		if(rAtosMed.getCbo() == null){
			/*
			   LPAD(NVL(decode(cbo,NULL,DECODE(FATC_BUSCA_CBO(P_CTH_SEQ,iph.seq,iph.pho_seq),NULL, '0',FATC_BUSCA_CBO(P_CTH_SEQ,iph.seq,iph.pho_seq)
			                                  ), CBO
			                  ),0
			           ),6,'0'
			       )  -- Marina 14/12/2009
			 */
			/*
			final String cbo = buscaCpfCboResponsavelPorIchRN.buscarCboResponsavel( cthSeq, 
																					  rAtosMed.getIphSeq(), 
																					  rAtosMed.getPhoSeq()
			
																					);
			 */
			return String.valueOf(0);
		} else {
			return rAtosMed.getCbo();
		}
	}

	protected BuscaCpfCboResponsavelPorIchRN getBuscaCpfCboResponsavelPorIchRN() {
		return buscaCpfCboResponsavelPorIchRN;
	}

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}

	protected LayoutArquivoSusRN getLayoutArquivoSusRN() {
		return layoutArquivoSusRN;
	}
	
	protected FatcBuscaServClassRN getFatcBuscaServClassRN(){
		return fatcBuscaServClassRN;
	}
}