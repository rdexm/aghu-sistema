package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.FichaPreOperatoriaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAreaTricProcCirg;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FichaPreOperatoriaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(FichaPreOperatoriaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private FichaPreOperatoriaRN fichaPreOperatoriaRN;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade iBlocoCirurgicoCadastroApoioFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2602336726448189350L;
	
	public enum FichaPreOperatoriaONExceptionCode implements	BusinessExceptionCode {
		ERRO_FICHA_PRE_OPERATORIA_DATA_CIRURGIA_NULA,MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}

	public List<FichaPreOperatoriaVO> listarProcedimentoPorCirurgia(
			Short seqUnidadeCirurgica, String unidadeCirurgica,
			Short seqUnidadeInternacao, Date dataCirurgia, Integer prontuario) throws ApplicationBusinessException {
		if(dataCirurgia == null){
			throw new ApplicationBusinessException(FichaPreOperatoriaONExceptionCode.ERRO_FICHA_PRE_OPERATORIA_DATA_CIRURGIA_NULA);
		}
		
		List<FichaPreOperatoriaVO> listVo = new ArrayList<FichaPreOperatoriaVO>();
		
		List<MbcProcEspPorCirurgias> listProcedimentoPorCirurgia = getMbcProcEspPorCirurgiasDAO().listarMbcProcEspPorCirurgiasPorDataCrgEUnidadeCrg(seqUnidadeCirurgica, seqUnidadeInternacao, dataCirurgia, prontuario);
		
		if(listProcedimentoPorCirurgia.isEmpty()){
			throw new ApplicationBusinessException(FichaPreOperatoriaONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		for (int i =0; i< listProcedimentoPorCirurgia.size(); i++) {
			MbcProcEspPorCirurgias procedimentoPorCirurgia  = listProcedimentoPorCirurgia.get(i);
			FichaPreOperatoriaVO vo = new FichaPreOperatoriaVO();
			
			vo.setDtCirurgia(procedimentoPorCirurgia.getCirurgia().getData());
			vo.setNome(procedimentoPorCirurgia.getCirurgia().getPaciente().getNome());
			vo.setIdade(getFichaPreOperatoriaRN().mbccIdadeExt(procedimentoPorCirurgia.getCirurgia().getPaciente().getDtNascimento(), procedimentoPorCirurgia.getCirurgia().getData()));
			vo.setProntuario(CoreUtil.formataProntuario(procedimentoPorCirurgia.getCirurgia().getPaciente().getProntuario()));
			
			AghUnidadesFuncionais unfPac = getAghuFacade()
					.pesquisarUnidadesFuncionaisPaciente(
							DominioPacAtendimento.S, null, procedimentoPorCirurgia.getCirurgia().getPaciente().getCodigo(),
							Boolean.FALSE).get(0);//Esta consulta sempre retornará registros porque é feito inner join na busca principal
			vo.setUnidade(getFichaPreOperatoriaRN().mbccGetunfDesc(unfPac.getSeq()));
			
			vo.setLocal(getEscalaCirurgiasON().pesquisaQuarto(procedimentoPorCirurgia.getCirurgia().getPaciente().getCodigo()));
			vo.setNroAgenda(procedimentoPorCirurgia.getCirurgia().getNumeroAgenda());
			if ((procedimentoPorCirurgia.getCirurgia().getDataPrevisaoInicio()) != null){
				vo.setDthrPrevInicio(procedimentoPorCirurgia.getCirurgia().getDataPrevisaoInicio());
			}
			vo.setUnidadeCirurgica(unidadeCirurgica);
			
			listarProcedimento(procedimentoPorCirurgia.getProcedimentoCirurgico().getDescricao(), vo);
			listarAreaTricotomia(procedimentoPorCirurgia.getProcedimentoCirurgico().getSeq(), vo);
			
			while((i+1<=listProcedimentoPorCirurgia.size()-1)
					    && (procedimentoPorCirurgia.getCirurgia().getSeq().equals(listProcedimentoPorCirurgia.get(i+1).getCirurgia().getSeq()))
						&& !(procedimentoPorCirurgia.getProcedimentoCirurgico().getSeq().equals(listProcedimentoPorCirurgia.get(i+1).getProcedimentoCirurgico().getSeq()))){
				listarProcedimento(listProcedimentoPorCirurgia.get(i+1).getProcedimentoCirurgico().getDescricao(), vo);
				listarAreaTricotomia(listProcedimentoPorCirurgia.get(i+1).getProcedimentoCirurgico().getSeq(), vo);
				i++;
			}
			
			listVo.add(vo);
		}
					
		CoreUtil.ordenarLista(listVo, FichaPreOperatoriaVO.Fields.DTHR_PREV_INICIO_ORDER.toString(), true);
		CoreUtil.ordenarLista(listVo, FichaPreOperatoriaVO.Fields.NRO_AGENDA_ORDER.toString(), true);
				
		return listVo;
	}
	

	public void listarAreaTricotomia(Integer pciSeq, FichaPreOperatoriaVO vo) {
 
		List<LinhaReportVO> listaTriagens= new ArrayList<LinhaReportVO>();
		
		List<MbcAreaTricProcCirg> areasTricotomia = getBlocoCirurgicoCadastroApoioFacade().buscarAreaTricPeloSeqProcedimento(pciSeq);
		for (MbcAreaTricProcCirg mbcAreaTricProcCirgs : areasTricotomia) {
			
			LinhaReportVO linhaReportTriagem = new LinhaReportVO();			
			linhaReportTriagem.setTexto1(mbcAreaTricProcCirgs.getMbcAreaTricotomia().getDescricao());
			
			listaTriagens.add(linhaReportTriagem);			
		}	
		
		vo.setTricotomiaList(listaTriagens);
	}

	public void listarProcedimento(String descricaoProcedimento, FichaPreOperatoriaVO vo) { 

		if(vo.getProcedimentoList() == null){
			vo.setProcedimentoList(Arrays.asList(new LinhaReportVO()));
		}

		LinhaReportVO linhaProcedimentoVO = vo.getProcedimentoList().get(vo.getProcedimentoList().size()-1);

		if (linhaProcedimentoVO.getTexto1() == null) {
			linhaProcedimentoVO.setTexto1(descricaoProcedimento);
		} else {
			if (linhaProcedimentoVO.getTexto2() == null) {
				linhaProcedimentoVO.setTexto2(descricaoProcedimento);
			} else {
				//if (linhaProcedimentoVO.getTexto3() == null) {
				linhaProcedimentoVO.setTexto3(descricaoProcedimento);
				//} else {
				// novaLinha
				linhaProcedimentoVO = new LinhaReportVO();
				linhaProcedimentoVO.setTexto1(descricaoProcedimento);
				//}
			}
		}
	}

	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected FichaPreOperatoriaRN getFichaPreOperatoriaRN() {
		return fichaPreOperatoriaRN;
	}
	
	protected IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return iBlocoCirurgicoCadastroApoioFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
}
