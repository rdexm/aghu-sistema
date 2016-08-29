package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.vo.ReceitasGeralEspecialVO;
import br.gov.mec.aghu.ambulatorio.vo.TDataVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamItemReceituarioId;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.view.VMamReceitas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;


@Stateless
public class ReceitasRN extends BaseBusiness{
	
	private static final long serialVersionUID = 3586806751920004146L;
	
	private static final Log LOG = LogFactory.getLog(ReceitasRN.class);
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private MamItemReceituarioDAO mamItemReceituarioDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	/*
	 * @ORADB p_gera_dados_data
	 */
	public List<TDataVO> pGeraDadosData(Integer pPacCodigo){
		final String PADRAO_DATA = "YYYYMM";
		
		Integer pacCodigo = pPacCodigo;
		String itemValue;
		
		Integer vLimTab = 3;
		Integer vAno;
		Integer vMes;
		String vData;
		Integer vPeriodoAtual;
		Integer vPeriodoAnt = -1;
		List<TDataVO> listaTDataVO = new ArrayList<TDataVO>();
		List<TDataVO> listaPeriodo = new ArrayList<TDataVO>();
		
		itemValue = "Cronológica";
		TDataVO tDataVOCrono = new TDataVO();
		tDataVOCrono.setHierarquia(1);
		tDataVOCrono.setDescricao(itemValue);
		listaTDataVO.add(tDataVOCrono);
		
		vData = DateUtil.obterDataFormatada(new Date(), PADRAO_DATA);
		
		TDataVO tDataVOPeriodo = new TDataVO();
		tDataVOPeriodo.setDataFinal(vData);
		
		vAno = Integer.valueOf(StringUtils.substring(vData, 0, 4));
		vMes = Integer.valueOf(StringUtils.substring(vData, 4, 6));
		
		if(vMes <= 5){
			vAno = vAno-1;
			vMes = 12 - 5 + vMes;
		}else{
			vMes = vMes - 5;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo.setDataInicial(vData);
		listaPeriodo.add(tDataVOPeriodo);
		
		if(vMes == 1){
			vAno = vAno - 1;
			vMes = 12;
		}else{
			vMes = vMes - 1;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		TDataVO tDataVOPeriodo2 = new TDataVO();
		tDataVOPeriodo2.setDataFinal(vData);
		
		if(vMes <= 5){
			vAno = vAno - 1;
			vMes = 12 - 5 + vMes;
		}else{
			vMes = vMes-5;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo2.setDataInicial(vData);
		listaPeriodo.add(tDataVOPeriodo2);
		
		if(vMes == 1){
			vAno = vAno - 1;
		}else{
			vMes = vMes - 1;
		}
		
		TDataVO tDataVOPeriodo3 = new TDataVO();

		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo3.setDataFinal(vData);
		
		if(vMes <= 5){
			vAno = vAno - 1;
			vMes = 12 - 5 + vMes;
		}else{
			vMes = vMes - 5;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo3.setDataInicial(vData);
		listaPeriodo.add(tDataVOPeriodo3);
		
		int contador = 1;
		
		for (TDataVO registro : listaPeriodo) {
			if(contador == vLimTab){
				registro.setDescricao("até ".concat(cMontaDescPeriodo(Integer.valueOf(registro.getDataFinal()))));
			}else{
				registro.setDescricao(cMontaDescPeriodo(Integer.valueOf(registro.getDataInicial())).concat(" até ").concat(cMontaDescPeriodo(Integer.valueOf(registro.getDataFinal()))));
			}
			contador++;
		}
		
		List<ReceitasGeralEspecialVO> listaRetorno = this.ambulatorioFacade.gerarDados(pacCodigo);
		
		for (ReceitasGeralEspecialVO registro : listaRetorno) {
			registro.setNome(ambulatorioFacade.obterDescricaoCidCapitalizada(obterRapcBuscaNome(registro.getMatricula(), registro.getVinculo()), CapitalizeEnum.TODAS));
			registro.setNomeEspecialidade(ambulatorioFacade.obterDescricaoCidCapitalizada(registro.getNomeEspecialidade(), CapitalizeEnum.TODAS));
			registro.setNomeReduzido(ambulatorioFacade.obterDescricaoCidCapitalizada(registro.getNomeReduzido(), CapitalizeEnum.TODAS));
		}
		
		ordenarListaP1(listaRetorno);
		
		for (ReceitasGeralEspecialVO receitasGeralEspecialVO : listaRetorno) {
			vPeriodoAtual = cDeterminaPeriodo(Integer.valueOf(DateUtil.obterDataFormatada(receitasGeralEspecialVO.getDataHoraCriacao(), PADRAO_DATA)), listaPeriodo);
			if(!vPeriodoAtual.equals(vPeriodoAnt)){
				itemValue = listaPeriodo.get(vPeriodoAtual).getDescricao();
				TDataVO tDataRegistro = new TDataVO();
				tDataRegistro.setHierarquia(2);
				tDataRegistro.setDescricao(itemValue);
				listaTDataVO.add(tDataRegistro);
				
				vPeriodoAnt = vPeriodoAtual;
			}
			
			itemValue = DateUtil.obterDataFormatada(receitasGeralEspecialVO.getDataHoraCriacao(), "dd/MM/yyyy").concat(" - ")
					.concat(receitasGeralEspecialVO.getNomeEspecialidade()).concat(" - ").concat(receitasGeralEspecialVO.getNome());
			
			TDataVO tDataRegistro2 = new TDataVO();
			tDataRegistro2.setHierarquia(3);
			tDataRegistro2.setDescricao(itemValue);
			tDataRegistro2.setConNumero(receitasGeralEspecialVO.getConNumero());
			listaTDataVO.add(tDataRegistro2);
		}
		
		return listaTDataVO;
	}
	
	/*
	 * @ORADB p_gera_dados_esp
	 */
	public List<TDataVO> pGerarDadosEspecialidade(Integer pPacCodigo){
		final String PADRAO_DATA = "YYYYMM";
		
		Integer pacCodigo = pPacCodigo;
		String itemValue;
		
		Integer vLimTab = 3;
		Integer vAno;
		Integer vMes;
		String vData;
		Integer vPeriodoAtual;
		Integer vPeriodoAnt = -1;
		Short vEspSeqAnt = 0;
		
		List<TDataVO> listaTDataVO = new ArrayList<TDataVO>();
		List<TDataVO> listaPeriodo = new ArrayList<TDataVO>();
		
		itemValue = "Especialidade";
		TDataVO tDataVOEspecialidade = new TDataVO();
		tDataVOEspecialidade.setHierarquia(1);
		tDataVOEspecialidade.setDescricao(itemValue);
		listaTDataVO.add(tDataVOEspecialidade);
		
		vData = DateUtil.obterDataFormatada(new Date(), PADRAO_DATA);
		
		TDataVO tDataVOPeriodo = new TDataVO();
		tDataVOPeriodo.setDataFinal(vData);
		
		vAno = Integer.valueOf(StringUtils.substring(vData, 0, 4));
		vMes = Integer.valueOf(StringUtils.substring(vData, 4, 6));
		
		if(vMes <= 5){
			vAno = vAno - 1;
			vMes = 12 - 5 + vMes;
		}else{
			vMes = vMes - 5;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo.setDataInicial(vData);
		listaPeriodo.add(tDataVOPeriodo);
		
		if(vMes == 1){
			vAno = vAno - 1;
			vMes = 12;
		}else{
			vMes = vMes - 1;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		TDataVO tDataVOPeriodo2 = new TDataVO();
		tDataVOPeriodo2.setDataFinal(vData);
		
		if(vMes <= 5){
			vAno = vAno - 1;
			vMes = 12-5+vMes;
		}else{
			vMes = vMes-5;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo2.setDataInicial(vData);
		listaPeriodo.add(tDataVOPeriodo2);
		
		if(vMes == 1){
			vAno = vAno - 1;
		}else{
			vMes = vMes - 1;
		}
		
		TDataVO tDataVOPeriodo3 = new TDataVO();

		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo3.setDataFinal(vData);
		
		if(vMes <= 5){
			vAno = vAno - 1;
			vMes = 12 - 5 + vMes;
		}else{
			vMes = vMes-5;
		}
		
		vData = vAno.toString().concat(StringUtil.adicionaZerosAEsquerda(vMes, 2));
		tDataVOPeriodo3.setDataInicial(vData);
		listaPeriodo.add(tDataVOPeriodo3);
		
		int contador = 1;
		
		for (TDataVO registro : listaPeriodo) {
			if(contador == vLimTab){
				registro.setDescricao("até ".concat(cMontaDescPeriodo(Integer.valueOf(registro.getDataFinal()))));
			}else{
				registro.setDescricao(cMontaDescPeriodo(Integer.valueOf(registro.getDataInicial())).concat(" até ").concat(cMontaDescPeriodo(Integer.valueOf(registro.getDataFinal()))));
			}
			contador++;
		}
		
		List<ReceitasGeralEspecialVO> listaRetorno = this.ambulatorioFacade.gerarDados(pacCodigo);
		
		for (ReceitasGeralEspecialVO registro : listaRetorno) {
			registro.setNome(ambulatorioFacade.obterDescricaoCidCapitalizada(obterRapcBuscaNome(registro.getMatricula(), registro.getVinculo()), CapitalizeEnum.TODAS));
			registro.setNomeEspecialidade(ambulatorioFacade.obterDescricaoCidCapitalizada(registro.getNomeEspecialidade(), CapitalizeEnum.TODAS));
			registro.setNomeReduzido(ambulatorioFacade.obterDescricaoCidCapitalizada(registro.getNomeReduzido(), CapitalizeEnum.TODAS));
		}
		
		ordenarListaP2(listaRetorno);
		
		for (ReceitasGeralEspecialVO receitasGeralEspecialVO : listaRetorno) {
			
			Short vEspSeqAtual = receitasGeralEspecialVO.getEspSeq();
			
			if(!vEspSeqAnt.equals(vEspSeqAtual)){
				TDataVO tDataRegistro = new TDataVO();
				tDataRegistro.setHierarquia(2);
				tDataRegistro.setDescricao(receitasGeralEspecialVO.getNomeEspecialidade());
				listaTDataVO.add(tDataRegistro);
				
				vEspSeqAnt = receitasGeralEspecialVO.getEspSeq();
				vPeriodoAtual = -1;
				vPeriodoAnt = -1;
			}

			vPeriodoAtual = cDeterminaPeriodo(Integer.valueOf(DateUtil.obterDataFormatada(receitasGeralEspecialVO.getDataHoraCriacao(), PADRAO_DATA)), listaPeriodo);

			if(!vPeriodoAtual.equals(vPeriodoAnt)){
				itemValue = listaPeriodo.get(vPeriodoAtual).getDescricao();
				TDataVO tDataRegistro = new TDataVO();
				tDataRegistro.setHierarquia(3);
				tDataRegistro.setDescricao(itemValue);
				listaTDataVO.add(tDataRegistro);
				
				vPeriodoAnt = vPeriodoAtual;
			}
			
			itemValue = DateUtil.obterDataFormatada(receitasGeralEspecialVO.getDataHoraCriacao(), "dd/MM/yyyy").concat(" - ").concat(receitasGeralEspecialVO.getNome());
			
			TDataVO tDataRegistro2 = new TDataVO();
			tDataRegistro2.setHierarquia(4);
			tDataRegistro2.setDescricao(itemValue);
			tDataRegistro2.setConNumero(receitasGeralEspecialVO.getConNumero());
			listaTDataVO.add(tDataRegistro2);
		}
		
		return listaTDataVO;
		
	}
	
	private Integer cDeterminaPeriodo(Integer pAnoMes, List<TDataVO> listaTDataVO){
		int vPeriodo = -1;
		int contador = 0;
		int vLimTab = 3;
		
		while(contador < vLimTab){
			if(pAnoMes >= Integer.valueOf(listaTDataVO.get(contador).getDataInicial()) 
					&& pAnoMes <= Integer.valueOf(listaTDataVO.get(contador).getDataFinal())){
				vPeriodo = contador;
			}
			contador++;
		}
		if(vPeriodo == -1){
			vPeriodo = 2;
		}
		
		return vPeriodo;
	}
	
	private String cMontaDescPeriodo(Integer pAnoMes){ 
		
		String vTexto;
		Integer vAno;
		Integer vMes;
		
		vAno = Integer.valueOf(StringUtils.substring(pAnoMes.toString(), 0, 4));
		vMes = Integer.valueOf(StringUtils.substring(pAnoMes.toString(), 5, 6));
		
		vTexto = this.obterMesCompetencia(vMes).concat("/").concat(vAno.toString());
		
		return vTexto;
	}
	
	private String obterMesCompetencia(Integer mes){
		switch (mes) {
		case 1:
			return "Jan";

		case 2:
			return "Fev";
			
		case 3:
			return "Mar";
			
		case 4:
			return "Abr";
			
		case 5:
			return "Mai";
			
		case 6:
			return "Jun";
			
		case 7:
			return "Jul";
			
		case 8:
			return "Ago";
			
		case 9:
			return "Set";
			
		case 10:
			return "Out";
			
		case 11:
			return "Nov";
			
		case 12:
			return "Dez";
			
		default:
			return "";
		}
	}
	
	private String obterRapcBuscaNome(Integer matricula, Short vinCodigo) {
		
		String nomeServidor = "";
		if(matricula != null && vinCodigo != null){
			 nomeServidor = registroColaboradorFacade.obterNomePessoaServidor(vinCodigo, matricula);
		}
		
		return nomeServidor;
	}
	
	private void ordenarListaP1(List<ReceitasGeralEspecialVO> listaRetorno){
		Collections.sort(listaRetorno, new Comparator<ReceitasGeralEspecialVO>() {
			public int compare(ReceitasGeralEspecialVO a, ReceitasGeralEspecialVO b){

				   return new CompareToBuilder()
				     .append(b.getDataOrd(), a.getDataOrd())
				     .append(a.getNomeEspecialidade(), b.getNomeEspecialidade())
				     .append(a.getNome(), b.getNome())
				     .toComparison();
				}			
		});
	}
	
	private void ordenarListaP2(List<ReceitasGeralEspecialVO> listaRetorno){
		Collections.sort(listaRetorno, new Comparator<ReceitasGeralEspecialVO>() {
			public int compare(ReceitasGeralEspecialVO a, ReceitasGeralEspecialVO b){
				   return new CompareToBuilder()
				     .append(a.getEspSeq(), b.getEspSeq())
				     .append(b.getDataOrd(), a.getDataOrd())
				     .append(a.getNome(), b.getNome())
				     .toComparison();
				}			
		});
	}
	
	public void gravarReceitas(Integer conNumero, VMamReceitas registro, MamReceituarios receituario,
			DominioTipoReceituario dominioTipoReceituario) throws ApplicationBusinessException{
		
		MamReceituarios receituarioAInserir = new MamReceituarios();

		if(receituario == null) {
			receituarioAInserir.setTipo(dominioTipoReceituario);
			AacConsultas consulta = this.aacConsultasDAO.obterPorChavePrimaria(conNumero);
			receituarioAInserir.setConsulta(consulta);
			receituarioAInserir.setPaciente(consulta.getPaciente());
			receituarioAInserir.setPendente(DominioIndPendenteAmbulatorio.P);
			receituarioAInserir.setIndImpresso(DominioSimNao.N);
			receituarioAInserir.setNroVias(Byte.valueOf("2"));
			receituarioAInserir.setServidor(this.registroColaboradorFacade.obterServidorPorUsuario(this.obterLoginUsuarioLogado()));
			receituarioAInserir.setDthrCriacao(new Date());
			mamReceituariosDAO.persistir(receituarioAInserir);

			gravarItemReceituario(receituarioAInserir, registro);
		}else{
			MamReceituarios receituarioOld = this.mamReceituariosDAO.obterPorChavePrimaria(receituario.getSeq());
			gravarItemReceituario(receituarioOld, registro);
		}
		
	}
	
	private void gravarItemReceituario(MamReceituarios receituarios, VMamReceitas registro){
		
		MamItemReceituario itemReceituarioAInserir = new MamItemReceituario();
		
		Short maxSeqP = this.ambulatorioFacade.obterValorMaxSeqP(receituarios.getSeq());
		Integer seqP;
		
		if(maxSeqP != null){
			seqP = maxSeqP + 1;
		}else{
			seqP = 1;
		}
		
		itemReceituarioAInserir.setId(new MamItemReceituarioId(receituarios.getSeq(), Short.valueOf(seqP.toString())));
		itemReceituarioAInserir.setOrdem(Byte.valueOf(seqP.toString()));
		itemReceituarioAInserir.setDescricao(registro.getDescricao());
		itemReceituarioAInserir.setFormaUso(registro.getFormaUso());
		itemReceituarioAInserir.setQuantidade(registro.getQuantidade());
		itemReceituarioAInserir.setIndInterno(DominioSimNao.getInstance(registro.getIndInterno()));
		itemReceituarioAInserir.setIndUsoContinuo(DominioSimNao.getInstance(registro.getIndUsoContinuo()));
		itemReceituarioAInserir.setIndSituacao(DominioSituacao.A);
		itemReceituarioAInserir.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		itemReceituarioAInserir.setNroGrupoImpressao(Byte.valueOf("1"));
		itemReceituarioAInserir.setIndValidadeProlongada(DominioSimNao.N);
	
		mamItemReceituarioDAO.persistir(itemReceituarioAInserir);
	}
}