package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesComCirurgiaPorUnidadeVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioPacientesComCirurgiaPorUnidadeON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesComCirurgiaPorUnidadeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 2396491815090457486L;

	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(String strPesquisa, Boolean somenteAtivos) {
		return getAghuFacade().listarUnidadesFuncionaisPorUnidadeExecutora(strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, somenteAtivos);
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(String strPesquisa, Boolean somenteAtivos) {
		return getAghuFacade().listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, somenteAtivos);
	}

	public List<LinhaReportVO> listarUnidadesFuncionaisPorUnidInternacao(String strPesquisa, Boolean ativo) {
		List<AghUnidadesFuncionais> unidades = getAghuFacade().listarUnidadesFuncionaisPorSeqDescricaoAndarAla(strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, ativo);
		List<LinhaReportVO> retorno = new ArrayList<LinhaReportVO>();
		for (AghUnidadesFuncionais linha : unidades) {
			LinhaReportVO vo = new LinhaReportVO();
			String descricao = "";
			if(linha.getDescricao() != null){
				descricao = descricao.concat(linha.getDescricao()).concat(" - ");
				vo.setTexto2(linha.getDescricao());
			}
			if(linha.getAndar() != null){
				descricao = descricao.concat(linha.getAndar().toString().concat(" "));
				vo.setNumero9(linha.getAndar());
			}
			if(linha.getIndAla() != null){
				descricao = descricao.concat(linha.getIndAla().toString());
				vo.setTexto3(linha.getIndAla().toString());
			}
			vo.setTexto1(descricao);
			vo.setNumero4(linha.getSeq());
			retorno.add(vo);
		}
		return retorno;
	}

	public Long listarUnidadesFuncionaisPorUnidInternacaoCount(String strPesquisa, Boolean ativo) {
		return getAghuFacade().listarUnidadesFuncionaisPorSeqDescricaoAndarAlaCount(strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, ativo);
	}

	public List<RelatorioPacientesComCirurgiaPorUnidadeVO> listarPacientesComCirurgia(Short seqUnidadeCirurgica, Short seqUnidadeInternacao, Date dataCirurgia) {
		
		List<RelatorioPacientesComCirurgiaPorUnidadeVO> listCirurgia = getMbcCirurgiasDAO().listarCirurgiasPorSeqUnidadeFuncionalData(seqUnidadeCirurgica, seqUnidadeInternacao, dataCirurgia);
		
		List<RelatorioPacientesComCirurgiaPorUnidadeVO> listVo = new ArrayList<RelatorioPacientesComCirurgiaPorUnidadeVO>();
		for (RelatorioPacientesComCirurgiaPorUnidadeVO vo : listCirurgia) {
			//RelatorioPacientesComCirurgiaPorUnidadeVO vo = new RelatorioPacientesComCirurgiaPorUnidadeVO();
			vo.setfInternacao(cfInternacaoFormula(vo.getAtdUnfSeq())); 
			vo.setQuarto(getEscalaCirurgiasON().pesquisaQuarto(vo.getPacCodigo()));
			vo.setDtHrCirurgia(vo.getDtHrCirurgia());
			vo.setSciSeqp(vo.getSciSeqp());
			//vo.setConvenio(cirurgia.getConvenioSaudePlano().getId().getCnvCodigo());
			vo.setPacProntuarioString(CoreUtil.formataProntuario(vo.getPacProntuario()));
			//vo.setPacNome(cirurgia.getPaciente().getNome());
			vo.setRequisicoes(preencherRequisicoes(vo.getSeqCirurgia()));
			vo.setNroAgenda(vo.getNroAgenda());
			vo.setCirurgiao(preencherListNomeCirurgiao(vo.getSeqCirurgia()));
			vo.setAnestesista(preencherListNomeAnestesista(vo.getSeqCirurgia()));
			vo.setProcedimento(preencherListPciDescricao(vo.getSeqCirurgia()));
			vo.setDataHora(new Date());
			vo.setTitulo(cfESCALAFormula(seqUnidadeCirurgica, dataCirurgia));
			vo.setQuartoOrder(gerarQuartoOrder(vo.getPacCodigo()));
			//vo.setAtdUnfSeq(cirurgia.getAtendimento().getUnidadeFuncional().getSeq());
			
			listVo.add(vo);
		}

		CoreUtil.ordenarLista(listVo, RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.CONVENIO.toString(), true);
		CoreUtil.ordenarLista(listVo, RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.PAC_PRONTUARIO.toString(), true);
		CoreUtil.ordenarLista(listVo, RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.PAC_NOME.toString(), true);
		CoreUtil.ordenarLista(listVo, RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.QUARTO.toString(), true);
		CoreUtil.ordenarLista(listVo, RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.QUARTO_ORDER.toString(), true);
		CoreUtil.ordenarLista(listVo, RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.DTHR_CIRURGIA.toString(), true);
		CoreUtil.ordenarLista(listVo, RelatorioPacientesComCirurgiaPorUnidadeVO.Fields.ATD_UNF_SEQ.toString(), true);
		
		return listVo;
	}
	
	private String preencherRequisicoes(Integer crgSeq) {
		String retorno = "";
		String nciDescricao = recuperarStringNciDescricao(crgSeq);
		String fSangue = cfSangueFormula(crgSeq);
		if(nciDescricao != null && !nciDescricao.isEmpty()){
			if(fSangue == null){
				retorno = nciDescricao.concat("\n");
			}else{
				retorno = nciDescricao.concat("\n").concat(fSangue);
			}
		}else{
			retorno = fSangue;
		}
		return retorno;
	}

	private Integer gerarQuartoOrder(Integer pacCodigo) {
		Integer retorno = 1;
		if(getEscalaCirurgiasON().pesquisaQuarto(pacCodigo).equals("_______")){
			retorno = 2;
		}
		return retorno;
	}

	private String preencherListPciDescricao(Integer crgSeq) {
		List<MbcProcEspPorCirurgias> listMbcProcEspPorCirurgias = getMbcProcEspPorCirurgiasDAO().listarMbcProcEspPorCirurgiasPorCrgSeqComOrder(crgSeq, false, false, false);
		String retorno = "";
		for (MbcProcEspPorCirurgias procEspPorCirurgias : listMbcProcEspPorCirurgias) {
			if(retorno.isEmpty()){
				retorno = retorno.concat(procEspPorCirurgias.getProcedimentoCirurgico().getDescricao());
			}else{
				retorno = retorno.concat("\n").concat(procEspPorCirurgias.getProcedimentoCirurgico().getDescricao());
			}
		}
		return retorno;
	}
	
	private String preencherListNomeAnestesista(Integer crgSeq) {
		DominioFuncaoProfissional[] listFuncaoProfissional = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.ANC, DominioFuncaoProfissional.ANP, DominioFuncaoProfissional.ANR};
		return preencherListNome(crgSeq, listFuncaoProfissional);
	}
	
	public String preencherListNomeCirurgiao(Integer crgSeq) {
		DominioFuncaoProfissional[] listFuncaoProfissional = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF};
		return preencherListNome(crgSeq, listFuncaoProfissional);
	}

	private String preencherListNome(Integer crgSeq,
			DominioFuncaoProfissional[] listFuncaoProfissional) {
		List<MbcProfCirurgias> listMbcProfCirurgias = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorCrgSeqFuncaoProfissional(crgSeq, listFuncaoProfissional,null);
		String retorno = "";
		for (MbcProfCirurgias profCirurgias : listMbcProfCirurgias) {
			if(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual() != null){
				if(retorno.isEmpty()){
					retorno = retorno.concat(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual());
				}else{
					retorno = retorno.concat("\n").concat(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual());
				}
			}else{
				if(retorno.isEmpty()){
					retorno = retorno.concat(profCirurgias.getServidorPuc().getPessoaFisica().getNome());
				}else{
					retorno = retorno.concat("\n").concat(profCirurgias.getServidorPuc().getPessoaFisica().getNome());
				}
			}
		}
		return retorno;
	}

	private String recuperarStringNciDescricao(Integer crgSeq) {
		String retorno = "";
		List<MbcSolicitacaoEspExecCirg> listMbcSolicitacaoEspExecCirg = getMbcSolicitacaoEspExecCirgDAO().listarMbcSolicitacaoEspExecCirgPorCrgSeq(crgSeq);
		for (MbcSolicitacaoEspExecCirg solicitacaoEspExecCirg : listMbcSolicitacaoEspExecCirg) {
			if(retorno.isEmpty()){
				retorno = retorno.concat(solicitacaoEspExecCirg.getMbcNecessidadeCirurgica().getDescricao());
			}else{
				retorno = retorno.concat("\n").concat(solicitacaoEspExecCirg.getMbcNecessidadeCirurgica().getDescricao());
			}
		}
		return retorno;
	}

	/**
	 * function CF_ESCALAFormula 
	 */
	public String cfESCALAFormula(Short unfSeq, Date dataCirurgia){
		String descricao = "";
		MbcControleEscalaCirurgica escala = getMbcControleEscalaCirurgicaDAO().obterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(unfSeq, dataCirurgia);
		if(escala != null && escala.getTipoEscala() != null){
			if(escala.getTipoEscala().equals(DominioTipoEscala.P)){
				descricao = "ESCALA PRÉVIA DE CIRURGIAS PARA ".concat(DateUtil.dataToString(dataCirurgia, "dd/MM/yyyy")).concat(" (PASSÍVEL DE ALTERAÇÕES)");
			}else if(escala.getTipoEscala().equals(DominioTipoEscala.D)){
				descricao = "ESCALA DEFINITIVA DE CIRURGIAS PARA ".concat(DateUtil.dataToString(dataCirurgia, "dd/MM/yyyy"));
			}
		}else{
			descricao = "AGENDA PREVISTA DE CIRURGIAS - ".concat(DateUtil.dataToString(dataCirurgia, "dd/MM/yyyy")).concat(" (PASSÍVEL DE ALTERAÇÕES)");
		}
		return descricao;
	}
	
	/**
	 * function CF_INTERNACAOFormula
	 */
	public String cfInternacaoFormula(Short unfSeq){
		AghUnidadesFuncionais undadeFuncional = new AghUnidadesFuncionais();
		undadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
		String descricao = "";
		if(undadeFuncional != null){
			descricao = undadeFuncional.getDescricao()
				.concat(" - ")
				.concat(undadeFuncional.getAndar().toString())
				.concat("º ANDAR - ALA ")
				.concat(undadeFuncional.getIndAla().getDescricao().toUpperCase());
		}else{
			descricao = "PACIENTES SEM INDICAÇÃO DE QUARTO/LEITO";
		}
		return descricao;
	}
	
	/**
	 * function CF_SANGUEFormula
	 */
	public String cfSangueFormula(Integer crgSeq){
		String descricao = null;
		Long count = getMbcSolicHemoCirgAgendadaDAO().mbcSolicHemoCirgAgendadaCountPorCrgSeq(crgSeq);
		if(count != null && count > 0){
			descricao = "BANCO DE SANGUE";
		}
		return descricao;
	}
	
	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return  mbcSolicHemoCirgAgendadaDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
	
	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgDAO() {
		return mbcSolicitacaoEspExecCirgDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}
}
