package br.gov.mec.aghu.exames.pesquisa.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.VAelExamesSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.VAelExamesSolicitacaoPacAtdDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExameSituacaoVO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.model.VAelExamesSolicitacaoPacAtd;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ExameSituacaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ExameSituacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private VAelExamesSolicitacaoDAO vAelExamesSolicitacaoDAO;

@Inject
private VAelExamesSolicitacaoPacAtdDAO vAelExamesSolicitacaoPacAtdDAO;

@Inject
private AelAmostrasDAO aelAmostrasDAO;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7927498350609129296L;

	public enum ExameSituacaoONExceptionCode implements
	BusinessExceptionCode {

		AEL_01012, //Pelo menos uma data deve ser informada.
		AEL_01013, //situação do exame é obrigatória.
		AEL_01014, //Data programada informada exige situação A_EXECUTAR ou A_COLETAR.
		AEL_01404,//Deve ser informado as duas datas de referência(período).
		AEL_01015,//Deve ser informada as datas de referência ou a data programada.
		AEL_01766,//Para situação Liberado as Datas de referência inicial e final (período) devem ser informadas
		AEL_01016,//Não esqueça de informar os critérios de seleção
		AEL_01767,//Para Pesquisas com a situação Liberado o período informado deve ser menor ou igual a {xxx} dia(s).
		AEL_03410,//Erro na recuperação do parâmetro P_DIAS_PESQ_EXAME_SIT_TELA.
		AEL_03411,//Erro na recuperação dos parâmetros P_SITUACAO_A_COLETAR, P_SITUACAO_EM_COLETA e P_SITUACAO_NA_AREA_EXECUTORA.
		AEL_03412;//Erro na recuperação dos parâmetros P_SITUACAO_A_COLETAR e  P_SITUACAO_A_EXECUTAR.

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}


	public List<VAelExamesSolicitacao> pesquisaExameSolicitacao(String descricao, AghUnidadesFuncionais unidadeExecutora) {
		List<VAelExamesSolicitacao> lista = getVAelExamesSolicitacaoDAO().pesquisaExameSolicitacao(descricao, unidadeExecutora);

		return lista; 
	}

	public Long countExameSolicitacaoPacAtend(AghUnidadesFuncionais unidadeExecutora, 
			Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, 
			AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame
			, DominioOrigemAtendimento origemAtendimento, DominoOrigemMapaAmostraItemExame origemMapaTrabalho) {

		//VAelExamesSolicitacaoPacAtdDAO
		return getVAelExamesSolicitacaoPacAtdDAO().countExameSolicitacaoPacAtend(unidadeExecutora, dtHrInicial, dtHrFinal, dtHrProgramado
				, convenio, situacao, nomeExame, origemAtendimento, origemMapaTrabalho);
	}


	public List<PesquisaExameSituacaoVO> pesquisaExameSolicitacaoPacAtend(AghUnidadesFuncionais unidadeExecutora, 
			Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, 
			AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame,
			DominioOrigemAtendimento origemAtendimento,
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws BaseException {

		//validações D01
		//this.validarFiltroPesquisaExameSolicitacaoPacAtend(dtHrInicial, dtHrFinal, dtHrProgramado, situacao, nomeExame);

		List<PesquisaExameSituacaoVO> returnList = null;

		VAelExamesSolicitacaoPacAtdDAO dao = getVAelExamesSolicitacaoPacAtdDAO();
		List<VAelExamesSolicitacaoPacAtd> listExSolicPacAtd = dao.pesquisaExameSolicitacaoPacAtd(unidadeExecutora, dtHrInicial
				, dtHrFinal, dtHrProgramado, convenio, situacao, nomeExame,
				origemAtendimento, origemMapaTrabalho, firstResult, maxResult, orderProperty, asc);

		returnList = new LinkedList<PesquisaExameSituacaoVO>();

		for (VAelExamesSolicitacaoPacAtd item : listExSolicPacAtd) {
			if(item !=null && item.getId()!=null){
				PesquisaExameSituacaoVO vo = new PesquisaExameSituacaoVO();

				vo.setDataHoraEvento(item.getDthrEvento());
				vo.setSolicitacao(item.getId().getSoeSeq());
				vo.setProntuario(item.getProntuario());
				vo.setNomePaciente(item.getNomePaciente());
				vo.setLocalizacao(item.getLocalizacao());
				vo.setExameMaterial(item.getId().getNomeExameMaterial());
				vo.setDataSolicitacao(item.getCriadoEm());
				vo.setSolicitante(item.getSoeServidorNome());
				vo.setDescricaoConvenio(item.getCnvDescricao());
				vo.setOrigem(item.getOrigem());
			
				returnList.add(vo);
			}
		}


		return returnList;
	}


	public List<PesquisaExameSituacaoVO> pesquisaExameSolicitacaoPacAtendRel(AghUnidadesFuncionais unidadeExecutora, 
			Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, 
			AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame,
			DominioOrigemAtendimento origemAtendimento,
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho) throws ApplicationBusinessException {

		List<PesquisaExameSituacaoVO> returnList =null;

		VAelExamesSolicitacaoPacAtdDAO dao = getVAelExamesSolicitacaoPacAtdDAO();
		List<VAelExamesSolicitacaoPacAtd> listExSolicPacAtd = dao.pesquisaExameSolicitacaoPacAtdRel(unidadeExecutora, dtHrInicial,dtHrFinal, dtHrProgramado, convenio, situacao, nomeExame,origemAtendimento,origemMapaTrabalho);


		/*
		 * Consultar amostra
		 */
		AelAmostrasDAO amostrasDAO = aelAmostrasDAO;
		AelSolicitacaoExames solicitacaoExames;


		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yy HH:mm");

		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

		returnList = new ArrayList<PesquisaExameSituacaoVO>();
		StringBuffer prontuario = null;
		for (VAelExamesSolicitacaoPacAtd item : listExSolicPacAtd) {
			if(item !=null && item.getId()!=null){
				solicitacaoExames = new AelSolicitacaoExames();
				solicitacaoExames.setSeq(item.getId().getSoeSeq());
				PesquisaExameSituacaoVO vo = new PesquisaExameSituacaoVO();
				//Busca Nro. Único
				List<AelAmostras> amostras = amostrasDAO.buscarAmostrasPorSolicitacaoExame(solicitacaoExames);

				if(amostras!= null && amostras.size()>0){
					vo.setNumUnidade(amostras.get(0).getNroUnico());
				}
				if(item.getDthrEvento()!=null){
					vo.setDataHoraEvento(item.getDthrEvento());
					vo.setDataHoraEventoRel(sdf1.format(item.getDthrEvento()));
				}

				vo.setSolicitacao(item.getId().getSoeSeq());
				if(item.getProntuario()!=null){
					prontuario = new StringBuffer(item.getProntuario().toString());
					prontuario.insert(prontuario.length()-1, "/");
					int zeros = 9 - prontuario.length();
					if(zeros != 0){
						for(int i = 0; i < zeros; i++){
							prontuario.insert(0, "0");
						}
					}
					vo.setProntuarioRel(prontuario.toString());
				}

				vo.setNomePaciente(item.getNomePaciente());

				vo.setLocalizacao(item.getLocalizacao());
				vo.setExameMaterial(item.getId().getNomeExameMaterial());
				if(item.getCriadoEm()!=null){
					vo.setDataSolicitacao(item.getCriadoEm());
					vo.setDataSolicRel(sdf2.format(item.getCriadoEm()));
				}
				vo.setSolicitante(item.getSoeServidorNome());
				vo.setDescricaoConvenio(item.getCnvDescricao());
				if(item.getOrigem()!=null){
					//DominioOrigemAtendimento origem = DominioOrigemAtendimento.getInstance(item.getOrigem());
					vo.setOrigemRel(item.getOrigem().getDescricao());
				}
				returnList.add(vo);
			}

		}

		return returnList;
	}

	private VAelExamesSolicitacaoPacAtdDAO getVAelExamesSolicitacaoPacAtdDAO() {
		return vAelExamesSolicitacaoPacAtdDAO;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void validarFiltroPesquisaExameSolicitacaoPacAtend(Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado, AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame) throws BaseException {
		//Quando a situação é liberado
		if(situacao != null && TipoSituacaoExame.LI.name().equals(situacao.getCodigo().trim())) {
			AghParametros aghParametro;

			aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PESQ_EXAME_SIT_TELA);

			//primeiro verificar se as datas de referências foram preenchidas
			if(dtHrInicial == null || dtHrFinal == null) {
				ExameSituacaoONExceptionCode.AEL_01766.throwException();
			}

			if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
				float diff = CoreUtil.diferencaEntreDatasEmDias(dtHrFinal, dtHrInicial);
				if (diff > aghParametro.getVlrNumerico().floatValue()) {
					ExameSituacaoONExceptionCode.AEL_01767.throwException(aghParametro.getVlrNumerico().intValue());
				}
			}
		}

		//Quando os campos data de referência, data programada,  exame e situação forem nulos 
		if(dtHrInicial == null && dtHrFinal == null && dtHrProgramado == null && nomeExame == null && situacao == null) {
			ExameSituacaoONExceptionCode.AEL_01016.throwException();
		}

		//No caso de uma das datas de referência serem preenchidas e a outra não
		if((dtHrInicial == null && dtHrFinal != null)
				|| (dtHrInicial != null && dtHrFinal == null)) {
			ExameSituacaoONExceptionCode.AEL_01404.throwException();
		}

		//Se ambas as datas de referência e a data programada forem preenchidas.
		if(dtHrInicial != null && dtHrFinal != null && dtHrProgramado != null) {
			ExameSituacaoONExceptionCode.AEL_01015.throwException();
		}

		//quando situação for nula
		if(situacao == null) {
			ExameSituacaoONExceptionCode.AEL_01013.throwException();
		}

		//Para o caso da data de início de referência e data programada forem nulas
		if(dtHrInicial == null && dtHrProgramado == null) {
			AghParametros aghParametro, aghParametro2, aghParametro3;

			aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
			aghParametro2 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA);
			aghParametro3 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
				
			List<String> params = Arrays.asList(aghParametro.getVlrTexto(), aghParametro2.getVlrTexto(), aghParametro3.getVlrTexto());

			if (!params.contains(situacao.getCodigo())) {
				ExameSituacaoONExceptionCode.AEL_01012.throwException();
			}
		}

		//Se o campo de data programada NÃO for nulo e situação NÃO for igual aos parâmetros:
		if(dtHrProgramado != null) {
			AghParametros aghParametro, aghParametro2;

			aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
			aghParametro2 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);

			List<String> params = Arrays.asList(aghParametro.getVlrTexto(), aghParametro2.getVlrTexto());

			if(!params.contains(situacao.getCodigo())) {
				ExameSituacaoONExceptionCode.AEL_01014.throwException();
			}	
		}
	} // validarFiltroPesquisaExameSolicitacaoPacAtend


	private enum TipoSituacaoExame {
		/**
		 * Liberado
		 */
		LI("Liberado");

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private String fields;

		private TipoSituacaoExame(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	protected VAelExamesSolicitacaoDAO getVAelExamesSolicitacaoDAO() {
		return vAelExamesSolicitacaoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

}
