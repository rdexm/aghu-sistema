package br.gov.mec.aghu.internacao.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.BaixasDiaPorEtniaVO;
import br.gov.mec.aghu.internacao.vo.BaixasDiaVO;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório de baixas por dia
 * 
 * @author tfelini
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class RelatorioBaixasDiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioBaixasDiaON.class);
	
	private enum RelatorioBaixasDiaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_GENERICA;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 2894852592652841090L;
	private static final Comparator<BaixasDiaVO> COMPARATOR_BAIXAS_DIA_VO = new Comparator<BaixasDiaVO>() {
		@Override
		public int compare(BaixasDiaVO o1, BaixasDiaVO o2) {
			
			if (o1 == null || o2 == null ||
					(o1).getOrigem() == null || (o2).getOrigem() == null || 
					(o1).getGrupoConvenio() == null || (o2).getGrupoConvenio() == null || 
					(o1).getNomePaciente() == null || (o2).getNomePaciente() == null) {
				return 0;
			}
			
			String origem1 = (o1).getOrigem();
			String origem2 = (o2).getOrigem();
			
			String convenio1 = (o1).getGrupoConvenio();
			String convenio2 = (o2).getGrupoConvenio();
			
			String nomePac1 = (o1).getNomePaciente();
			String nomePac2 = (o2).getNomePaciente();

			/*
			ComparatorChain comparatorChain = new ComparatorChain();

			comparatorChain.addComparator(new BeanComparator("origem"));
			comparatorChain.addComparator(new BeanComparator("grupoConvenio"));
			comparatorChain.addComparator(new BeanComparator("nomePaciente"));
			*/
			//comparatorc
			
			//return comparatorChain.compare(o1, o2);
			
			if (origem1.substring(0,1).equals("4")){
				return -1;
			}else if (!origem1.equals(origem2) ){
				return origem1.compareTo(origem2);
			}else if (!convenio1.equals(convenio2)){
				return convenio1.compareTo(convenio2);
			}else {
				return nomePac1.compareTo(nomePac2);
			}
		}
	};
	
	public List<BaixasDiaPorEtniaVO> pesquisaPorEtnia(Date dataReferencia, 
			DominioGrupoConvenio grupoConvenio, AghOrigemEventos 
			origemEvento, AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException{
		
		List<BaixasDiaPorEtniaVO> listaPorEtnia = new ArrayList<BaixasDiaPorEtniaVO>(0);
		
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
		Integer idTipoMovimentacao = parametro.getVlrNumerico() == null ? null : parametro.getVlrNumerico().intValue();

		List<AinMovimentosInternacao> resMovInt = getAinMovimentoInternacaoDAO().pesquisaBaixasDia(dataReferencia, grupoConvenio,
				origemEvento, idTipoMovimentacao, etniaPaciente, exibeEtnia);
		
		if(resMovInt.isEmpty()){
			throw new ApplicationBusinessException(RelatorioBaixasDiaONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_GENERICA);
		}
		
		AghParametros parametroSigla = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_REP_BAIXAS_DIA_ORIGEM_SIGLA);
		AghParametros parametroDesc = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_REP_BAIXAS_DIA_ORIGEM_DESC);

		List<AipEtnia> listaEtnias = new ArrayList<AipEtnia>();
		for (AinMovimentosInternacao ainMov : resMovInt) {
			if(listaEtnias.isEmpty() || !listaEtnias.contains(ainMov.getInternacao().getPaciente().getEtnia())){
				listaEtnias.add(ainMov.getInternacao().getPaciente().getEtnia());
			}
		}

		for (AipEtnia etnia : listaEtnias) {

			List<AinMovimentosInternacao> listaMovIntEtnia = new ArrayList<AinMovimentosInternacao>();
			for (AinMovimentosInternacao ainMovInt : resMovInt) {
				if(ainMovInt.getInternacao().getPaciente().getEtnia().getId() == etnia.getId()){
					listaMovIntEtnia.add(ainMovInt);
				}
			}
										
			List<BaixasDiaVO> lista = new ArrayList<BaixasDiaVO>(0);
			lista = this.montaDadosBaixaDia(lista, listaMovIntEtnia, parametroDesc, parametroSigla);
			
			BaixasDiaPorEtniaVO porEtniaVO = new BaixasDiaPorEtniaVO();
			porEtniaVO.setId(etnia.getId());
			porEtniaVO.setDescricao(etnia.getDescricao());
			porEtniaVO.setListaBaixasPorEtnia(lista);

			listaPorEtnia.add(porEtniaVO);
		}
		
		return listaPorEtnia;
		
	}
	
	public List<BaixasDiaVO> pesquisa(Date dataReferencia, 
			DominioGrupoConvenio grupoConvenio, AghOrigemEventos 
			origemEvento, AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException{
		
		List<BaixasDiaVO> lista = new ArrayList<BaixasDiaVO>(0);

		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
		Integer idTipoMovimentacao = parametro.getVlrNumerico() == null ? null : parametro.getVlrNumerico().intValue();

		List<AinMovimentosInternacao> resMovInt = getAinMovimentoInternacaoDAO().pesquisaBaixasDia(dataReferencia, grupoConvenio,
				origemEvento, idTipoMovimentacao, etniaPaciente, exibeEtnia);
		
		if(resMovInt.isEmpty()){
			throw new ApplicationBusinessException(RelatorioBaixasDiaONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_GENERICA);
		}

		AghParametros parametroSigla = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_REP_BAIXAS_DIA_ORIGEM_SIGLA);
		AghParametros parametroDesc = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_REP_BAIXAS_DIA_ORIGEM_DESC);
		
		return this.montaDadosBaixaDia(lista, resMovInt, parametroDesc, parametroSigla);
		
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private List<BaixasDiaVO> montaDadosBaixaDia(List<BaixasDiaVO> lista, List<AinMovimentosInternacao> resMovInt, AghParametros parametroDesc, AghParametros parametroSigla) {

		Iterator<AinMovimentosInternacao> itMovInt = resMovInt.iterator();
		
		while (itMovInt.hasNext()){
			AinMovimentosInternacao movimentoInternacao = itMovInt.next();
			BaixasDiaVO baixaDia = new BaixasDiaVO();
			
			if (movimentoInternacao.getEspecialidade()!=null && movimentoInternacao.getEspecialidade().getSigla() !=null){
				Boolean siglaEspecial = false;
				String[] siglas = parametroSigla.getVlrTexto().split(";");
				
				for (int i = 0; i < siglas.length ; i++) {
					if (siglas[i].equals(movimentoInternacao.getEspecialidade().getSigla())){
						siglaEspecial = true;
					}
				}				
				if (siglaEspecial){
					baixaDia.setOrigem(parametroDesc.getVlrTexto());
				}else{
					if (movimentoInternacao.getInternacao() != null
							&& movimentoInternacao.getInternacao().getOrigemEvento()!= null){
						baixaDia.setOrigem(movimentoInternacao.getInternacao().getOrigemEvento().getDescricao());
					}
				}
				baixaDia.setSiglaEspecialidade(movimentoInternacao.getEspecialidade().getSigla());
			}
			
			if(movimentoInternacao.getInternacao()!=null){
				if (movimentoInternacao.getInternacao().getConvenioSaude()!=null 
						&& movimentoInternacao.getInternacao().getConvenioSaude().getGrupoConvenio()!=null){
					baixaDia.setGrupoConvenio(movimentoInternacao.getInternacao().getConvenioSaude().getGrupoConvenio().getDescricao());
				}
				if (movimentoInternacao.getInternacao().getPaciente() != null){
					if (movimentoInternacao.getInternacao().getPaciente().getProntuario()!=null){
						String prontAux = CoreUtil.formataProntuario(movimentoInternacao.getInternacao().getPaciente().getProntuario().toString());
						baixaDia.setProntuario(prontAux );						
					}
					if (movimentoInternacao.getInternacao().getPaciente().getNome() != null){
						baixaDia.setNomePaciente(movimentoInternacao.getInternacao().getPaciente().getNome());
					}
					if (movimentoInternacao.getInternacao().getPaciente().getDtNascimento() != null){
						baixaDia.setDataNascimento(movimentoInternacao.getInternacao().getPaciente().getDtNascimento());
					}
				}
				if (movimentoInternacao.getInternacao().getAtendimento() != null
						&& movimentoInternacao.getInternacao().getAtendimento().getDthrInicio() != null){
					baixaDia.setDataIngresso(movimentoInternacao.getInternacao().getAtendimento().getDthrInicio());
				}
				if (movimentoInternacao.getLeito() != null 
						&& movimentoInternacao.getLeito().getLeitoID() != null ){
					baixaDia.setLocal(movimentoInternacao.getLeito().getLeitoID());
				}else if(movimentoInternacao.getQuarto() != null 
						&& movimentoInternacao.getQuarto().getNumero() != null){
					baixaDia.setLocal(movimentoInternacao.getQuarto().getDescricao());
				}else if (movimentoInternacao.getUnidadeFuncional() != null 
						&& movimentoInternacao.getUnidadeFuncional().getAndar()!=null
						&& movimentoInternacao.getUnidadeFuncional().getIndAla() != null){
					String local = StringUtils.leftPad(movimentoInternacao.getUnidadeFuncional().getAndar().toString(),2,"0") 
									+  " " 
									+ movimentoInternacao.getUnidadeFuncional().getIndAla().toString();
					baixaDia.setLocal(local);
					
				}
				if (movimentoInternacao.getInternacao().getTipoCaracterInternacao()!= null){
					String tipoCaracterInternacao = movimentoInternacao.getInternacao().getTipoCaracterInternacao().getDescricao();
					if (tipoCaracterInternacao.length()>=9){
						baixaDia.setCaraterInternacao(tipoCaracterInternacao.substring(0, 9));
					}else{
						baixaDia.setCaraterInternacao(tipoCaracterInternacao);
					}
					
				}
				if(movimentoInternacao.getInternacao().getServidorProfessor()!=null){
					String numeroConselho = getPesquisaInternacaoFacade().buscarNroRegistroConselho(
							movimentoInternacao.getInternacao().getServidorProfessor().getId().getVinCodigo(), 
							movimentoInternacao.getInternacao().getServidorProfessor().getId().getMatricula());
					
					String nomeProfessor = getPesquisaInternacaoFacade().buscarNomeUsual(
							movimentoInternacao.getServidor().getId().getVinCodigo(), 
							movimentoInternacao.getServidor().getId().getMatricula());
					
					baixaDia.setCrmResponsavel(numeroConselho);
					baixaDia.setNomeResponsavel(nomeProfessor);
				}
				if (movimentoInternacao.getInternacao().getIndDifClasse()!= null){
					if (movimentoInternacao.getInternacao().getIndDifClasse()){
						baixaDia.setDc("S");
					}else{
						baixaDia.setDc("N");
					}
				}
				if (movimentoInternacao.getInternacao().getItemProcedimentoHospitalar()!=null){
					baixaDia.setCodFat(movimentoInternacao.getInternacao().getItemProcedimentoHospitalar().getCodTabela());
				}
				Integer ultimaDiariaAutorizada = null;
				
				if (movimentoInternacao.getInternacao() != null && movimentoInternacao.getInternacao().getDiariasAutorizadas() != null){
					ultimaDiariaAutorizada = movimentoInternacao.getInternacao().getDiariasAutorizadas().size();
				}
				if (ultimaDiariaAutorizada != null && ultimaDiariaAutorizada>0){
					baixaDia.setSenha(movimentoInternacao.getInternacao().getDiariasAutorizadas()
							.get(ultimaDiariaAutorizada-1).getSenha());
				}
			}
			lista.add(baixaDia);
		}

		Collections.sort(lista, COMPARATOR_BAIXAS_DIA_VO);
		
		return lista;
	}
		
	protected IParametroFacade getParametroFacade() {
		return parametroFacade; 
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return pesquisaInternacaoFacade;
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO(){
		return ainMovimentoInternacaoDAO; 
	}
}
